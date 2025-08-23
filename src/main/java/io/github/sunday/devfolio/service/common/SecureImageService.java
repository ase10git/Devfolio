package io.github.sunday.devfolio.service.common;

import io.github.sunday.devfolio.dto.common.ImageUploadResult;
import io.github.sunday.devfolio.dto.common.MultipartImage;
import io.github.sunday.devfolio.enums.ImageTarget;
import io.github.sunday.devfolio.exception.common.ImageUploadException;
import io.github.sunday.devfolio.exception.common.ImageValidationException;
import io.github.sunday.devfolio.exception.common.IncorrectImageTargetException;
import lombok.RequiredArgsConstructor;
import marvin.image.MarvinImage;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * 이미지 파일을 검증하는 공통 서비스
 */
@Service
@RequiredArgsConstructor
public class SecureImageService {

    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    private final Tika tika = new Tika();
    private final S3Service s3Service;

    public ImageUploadResult uploadTempImage(MultipartFile file, String target, Long userIdx) throws Exception {
        String targetPath = ImageTarget.fromFieldName(target).getTargetName();
        if (targetPath == null) throw new IncorrectImageTargetException("incorrect image target path name");
        String filePath = userIdx + "/" + targetPath;

        return uploadImage(file, filePath, true);
    }

    /**
     * AWS S3에 파일 검증 후 이미지 파일 업로드
     */
    public ImageUploadResult uploadImage(MultipartFile file, String filePath, boolean isTemp) throws ImageUploadException {
        try {
            // 파일 검증
            validateFile(file);

            // 안전한 파일명 생성
            String safeFileName = generateSafeFileName(file.getOriginalFilename());
            String fileFormatName = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);

            // 이미지 리사이징
            MultipartFile resizedImage = resizeImage(file, fileFormatName);

            // S3 업로드
            String fileFullPath = String.format("%s/%s", filePath, safeFileName);

            String imageUrl = s3Service.uploadFile(resizedImage, fileFullPath, isTemp);

            return ImageUploadResult.builder()
                    .originalFileName(file.getOriginalFilename())
                    .s3Key(fileFullPath)
                    .imageUrl(imageUrl)
                    .fileSize(file.getSize())
                    .uploadedAt(ZonedDateTime.now())
                    .build();

        } catch (Exception e) {
            throw new ImageUploadException("이미지 업로드 실패", e);
        }
    }

    /**
     * 파일 유효성 검사
     */
    private void validateFile(MultipartFile file) throws IOException {
        // 파일 존재 여부
        if (file == null || file.isEmpty()) {
            throw new ImageValidationException("파일이 없습니다.");
        }

        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ImageValidationException("파일 크기는 10MB 이하여야 합니다.");
        }

        // 파일 확장자 검증
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ImageValidationException("허용되지 않은 파일 형식입니다.");
        }

        // MIME 타입 검증 (Magic Number 기반)
        String detectedMimeType = tika.detect(file.getInputStream());
        if (!ALLOWED_MIME_TYPES.contains(detectedMimeType)) {
            throw new ImageValidationException("허용되지 않은 파일 형식입니다.");
        }

        // 실제 이미지 파일인지 검증
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new ImageValidationException("유효한 이미지 파일이 아닙니다.");
            }

            // 이미지 크기 제한
            if (image.getWidth() > 5000 || image.getHeight() > 5000) {
                throw new ImageValidationException("이미지 크기는 5000x5000 픽셀 이하여야 합니다.");
            }
        } catch (IOException e) {
            throw new ImageValidationException("이미지 파일을 읽을 수 없습니다.");
        }
    }

    /**
     * 파일 이름 생성하기
     */
    private String generateSafeFileName(String originalFileName) {
        String baseName = FilenameUtils.getBaseName(originalFileName);
        String extension = FilenameUtils.getExtension(originalFileName);

        // 특수문자 제거
        baseName = baseName.replaceAll("[^a-zA-Z0-9가-힣._-]", "");

        // UUID 추가로 중복 방지
        return String.format("%s_%s.%s",
                baseName,
                UUID.randomUUID().toString().substring(0, 8),
                extension.toLowerCase());
    }

    /**
     * 이미지 크기 조정
     */
    private MultipartFile resizeImage(MultipartFile file, String extensionName) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        // 최대 너비/높이 설정
        int maxDimension = 1920;

        if (originalImage.getWidth() <= maxDimension &&
                originalImage.getHeight() <= maxDimension) {
            return file;
        }

        // 비율 유지하며 리사이징
        double scale = Math.min(
                (double) maxDimension / originalImage.getWidth(),
                (double) maxDimension / originalImage.getHeight()
        );

        int newWidth = (int) (originalImage.getWidth() * scale);
        int newHeight = (int) (originalImage.getHeight() * scale);

        MarvinImage marvinImage = new MarvinImage(originalImage);

        Scale imageScale = new Scale();
        imageScale.load();
        imageScale.setAttribute("newWidth", newWidth);
        imageScale.setAttribute("newHeight", newHeight);
        imageScale.process(marvinImage.clone(), marvinImage, null, null, false);

        BufferedImage resizedImage = marvinImage.getBufferedImageNoAlpha();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, extensionName, baos);
        baos.flush();

        return MultipartImage.builder()
                .bytes(baos.toByteArray())
                .build();
    }
}
