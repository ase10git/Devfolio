package io.github.sunday.devfolio.service.portfolio;

import io.github.sunday.devfolio.dto.common.ImageUploadResult;
import io.github.sunday.devfolio.dto.portfolio.PortfolioWriteRequestDto;
import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioImage;
import io.github.sunday.devfolio.exception.common.ImageUploadException;
import io.github.sunday.devfolio.repository.portfolio.PortfolioImageRepository;
import io.github.sunday.devfolio.service.common.S3Service;
import io.github.sunday.devfolio.service.common.SecureImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URLDecoder;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * 포트폴리오의 이미지 Entity를 관리하는 Service
 */
@Service
@RequiredArgsConstructor
public class PortfolioImageService {
    private final PortfolioImageRepository portfolioImageRepository;
    private final SecureImageService secureImageService;
    private final S3Service s3Service;

    /**
     * 포트폴리오의 썸네일 이미지 조회
     */
    public PortfolioImage getPortfolioThumbnail(Long portfolioIdx) {
        return portfolioImageRepository.findByPortfolio_PortfolioIdxAndIsThumbnailTrue(portfolioIdx).orElse(null);
    }

    public List<PortfolioImage> getPortfolioImages(Long portfolioIdx) {
        return portfolioImageRepository.findAllByPortfolio_PortfolioIdx(portfolioIdx);
    }

    /**
     * 포트폴리오 이미지 추가
     * DB에 Entity 추가 및 AWS S3에 이미지 파일 업로드
     */
    public void addPortfolioImage(Portfolio portfolio, PortfolioWriteRequestDto writeRequestDto, Long userIdx) throws Exception {
        String filePath = userIdx + "/portfolio/" + portfolio.getPortfolioIdx();

        MultipartFile thumbnailFile = writeRequestDto.getThumbnail();
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            PortfolioImage thumbnailImage = addNewImage(thumbnailFile, filePath, true);
            // DB에 이미지 추가
            thumbnailImage.setPortfolio(portfolio);
            portfolioImageRepository.save(thumbnailImage);
        }

        List<String> imageList = writeRequestDto.getImages();
        if (imageList != null && !imageList.isEmpty()) {
            imageList.forEach(imageUrl -> {
                try {
                    PortfolioImage imageInEditor = PortfolioImage.builder()
                            .portfolio(portfolio)
                            .imageUrl(imageUrl)
                            .s3Key(extractKeyFromUrl(imageUrl))
                            .isThumbnail(false)
                            .createdAt(ZonedDateTime.now())
                            .expireAt(ZonedDateTime.now().plusMonths(1))
                            .build();
                    portfolioImageRepository.save(imageInEditor);
                    s3Service.updateObjectTags(imageInEditor.getS3Key(), "lifecycle", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 포트폴리오 이미지 제거
     * DB Entity와 AWS S3의 파일 제거
     */
    public void deleteImageList(Long portfolioIdx, List<Long> imageIdxList) {
        String filePath = "/portfolio/" + portfolioIdx;
        imageIdxList.forEach(imageIdx -> deleteImage(imageIdx, filePath));
    }

    // Todo : 이미지 수정 로직 추가
    
    /**
     * AWS S3에 이미지 추가하기
     */
    // Todo : 예외 처리 로직 추가
    private PortfolioImage addNewImage(MultipartFile file, String filePath, boolean isThumbnail) throws ImageUploadException{
        ImageUploadResult uploadResult = secureImageService.uploadImage(file, filePath, false);

        return PortfolioImage.builder()
                .imageUrl(uploadResult.getImageUrl())
                .s3Key(uploadResult.getS3Key())
                .isThumbnail(isThumbnail)
                .createdAt(ZonedDateTime.now())
                .expireAt(ZonedDateTime.now().plusMonths(1))
                .build();
    }

    private void deleteImage(Long imageIdx, String filePath) {
        PortfolioImage image = portfolioImageRepository.findById(imageIdx).orElse(null);
        if (image == null) return;
        s3Service.deleteFile(fullFilePath(filePath, image.getS3Key()));
        portfolioImageRepository.deleteById(imageIdx);
    }

    private String fullFilePath(String filePath, String fileName) {
        return filePath + "/" + URLDecoder.decode(fileName);
    }

    private String extractKeyFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 URL 형식입니다.", e);
        }
    }
}
