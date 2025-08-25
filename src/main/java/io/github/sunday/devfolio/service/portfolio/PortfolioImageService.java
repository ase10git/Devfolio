package io.github.sunday.devfolio.service.portfolio;

import io.github.sunday.devfolio.dto.common.ImageUploadResult;
import io.github.sunday.devfolio.dto.portfolio.PortfolioEditRequestDto;
import io.github.sunday.devfolio.dto.portfolio.PortfolioWriteRequestDto;
import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioImage;
import io.github.sunday.devfolio.exception.common.ImageUploadException;
import io.github.sunday.devfolio.repository.portfolio.PortfolioImageRepository;
import io.github.sunday.devfolio.service.common.S3Service;
import io.github.sunday.devfolio.service.common.SecureImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
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
     * 포트폴리오 이미지 수정
     */
    @Transactional
    public void editPortfolioImage(Portfolio portfolio, PortfolioEditRequestDto editRequestDto, Long userIdx) throws Exception {
        String filePath = userIdx + "/portfolio/" + portfolio.getPortfolioIdx();
        PortfolioImage originalThumbnail = portfolioImageRepository
                .findByPortfolio_PortfolioIdxAndIsThumbnailTrue(portfolio.getPortfolioIdx()).orElse(null);

        // 썸네일 제거 동작
        if (editRequestDto.isRemoveFlag() && originalThumbnail != null) {
            deleteImage(originalThumbnail.getImageIdx());
        }

        // 썸네일 이미지 추가
        MultipartFile thumbnailFile = editRequestDto.getThumbnail();
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            PortfolioImage thumbnailImage = addNewImage(thumbnailFile, filePath, true);
            // DB에 이미지 추가
            thumbnailImage.setPortfolio(portfolio);
            portfolioImageRepository.save(thumbnailImage);
        }

        // 요청으로 온 이미지 목록
        List<String> imageList = editRequestDto.getImages();

        // 기존 목록 조회
        List<PortfolioImage> originalImageList = getPortfolioImages(portfolio.getPortfolioIdx()).stream()
                .filter(image -> !image.getIsThumbnail())
                .toList();
        List<String> originalUrlList = originalImageList.stream()
                .filter(image -> !image.getIsThumbnail())
                .map(image -> image.getImageUrl())
                .toList();

        // 기존 이미지 목록에서 이미지 제거
        if (!originalImageList.isEmpty()) {
            if (imageList == null || imageList.isEmpty()) {
                originalImageList.stream()
                        .forEach(image -> deleteImage(image.getImageIdx()));
            } else {
                originalImageList.stream()
                        .filter(image -> imageList.stream()
                                .noneMatch(requestUrl -> extractKeyFromUrl(requestUrl).equals(image.getS3Key())))
                        .forEach(image -> deleteImage(image.getImageIdx()));
            }
        }

        if (imageList != null && !imageList.isEmpty()) {
            // 새 이미지 목록 추가
            imageList
                    .stream()
                    .filter(imageUrl -> !originalUrlList.contains(imageUrl))
                    .forEach(imageUrl -> {
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

    /**
     * 포트폴리오의 이미지 모두 제거
     */
    public void deleteImages(Long portfolioIdx) {
        List<PortfolioImage> images = getPortfolioImages(portfolioIdx);
        if (images != null && !images.isEmpty()) {
            images.forEach(image -> deleteImage(image.getImageIdx()));
        }
    }

    @Transactional
    private void deleteImage(Long imageIdx) {
        PortfolioImage image = portfolioImageRepository.findById(imageIdx).orElse(null);
        if (image == null) return;
        s3Service.deleteFile(image.getS3Key());
        portfolioImageRepository.deleteById(imageIdx);
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
