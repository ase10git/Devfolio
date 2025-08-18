package io.github.sunday.devfolio.service.portfolio;

import io.github.sunday.devfolio.dto.common.ImageUploadResult;
import io.github.sunday.devfolio.dto.portfolio.PortfolioImageDto;
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
     * 포트폴리오의 모든 이미지 조회
     */
    public List<PortfolioImageDto> getPortfolioImages(Long portfolioIdx) {
        return portfolioImageRepository.findAllByPortfolio_PortfolioIdx(portfolioIdx)
                .stream()
                .map(this::imageToDto)
                .toList();
    }

    /**
     * 포트폴리오 이미지 추가
     * DB에 Entity 추가 및 AWS S3에 이미지 파일 업로드
     */
    public void addPortfolioImage(Portfolio portfolio, PortfolioWriteRequestDto writeRequestDto, Long userIdx) throws Exception {
        String filePath = userIdx + "/portfolio/" + portfolio.getPortfolioIdx();
        PortfolioImage thumbnailImage = addNewImage(writeRequestDto.getThumbnail(), filePath, true);
        List<String> imageList = writeRequestDto.getImages();

        // DB에 이미지 추가
        // Todo : Image Entity 추가가 안되는 에러 수정
        savePortfolioImage(portfolio, thumbnailImage);
        //imageList.forEach(image -> savePortfolioImage(portfolio, image));
    }

    /**
     * DB에 포트폴리오 Entity 저장하기
     */
    private void savePortfolioImage(Portfolio portfolio, PortfolioImage image) {
        image.setPortfolio(portfolio);
        portfolioImageRepository.save(image);
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

    /**
     * AWS S3에 이미지 리스트 추가하기
     */
    private List<PortfolioImage> addNewImageList(List<MultipartFile> files, String filePath) {
        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> {
                    try {
                        return addNewImage(file, filePath, false);
                    } catch (Exception e) {
                        // Todo : custom error 추가
                        throw new RuntimeException("이미지 업로드 실패: " + file.getOriginalFilename());
                    }
                })
                .toList();
    }

    private void deleteImage(Long imageIdx, String filePath) {
        PortfolioImage image = portfolioImageRepository.findById(imageIdx).orElse(null);
        if (image == null) return;
        s3Service.deleteFile(fullFilePath(filePath, image.getS3Key()));
        portfolioImageRepository.deleteById(imageIdx);
    }

    private PortfolioImageDto imageToDto(PortfolioImage image) {
        return PortfolioImageDto.builder()
                .imageIdx(image.getImageIdx())
                .portfolioIdx(image.getPortfolio().getPortfolioIdx())
                .imageUrl(image.getImageUrl())
                .isThumbnail(image.getIsThumbnail())
                .build();
    }

    private String fullFilePath(String filePath, String fileName) {
        return filePath + "/" + URLDecoder.decode(fileName);
    }
}
