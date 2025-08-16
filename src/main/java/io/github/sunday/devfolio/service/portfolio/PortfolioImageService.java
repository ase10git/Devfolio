package io.github.sunday.devfolio.service.portfolio;

import io.github.sunday.devfolio.dto.portfolio.PortfolioImageDto;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioImage;
import io.github.sunday.devfolio.repository.portfolio.PortfolioImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 포트폴리오의 이미지 Entity를 관리하는 Service
 */
@Service
@RequiredArgsConstructor
public class PortfolioImageService {
    private final PortfolioImageRepository portfolioImageRepository;
    
    // Todo : 포트폴리오의 이미지 조회
    public List<PortfolioImageDto> getPortfolioImages(Long portfolioIdx) {
        return portfolioImageRepository.findAllByPortfolio_PortfolioIdx(portfolioIdx)
                .stream()
                .map(this::imageToDto)
                .toList();
    }
    
    // Todo : 포트폴리오의 이미지 추가
    public void addNewImages() {
        // 이미지 추가 동작 - AWS S3 연동
    }
    
    
    // Todo : 포트폴리오의 이미지 제거
    public void removeImages() {
        // 이미지 삭제 동작 - AWS S3 연동
    }

    private PortfolioImageDto imageToDto(PortfolioImage image) {
        return PortfolioImageDto.builder()
                .imageIdx(image.getImageIdx())
                .portfolioIdx(image.getPortfolio().getPortfolioIdx())
                .imageUrl(image.getImageUrl())
                .isThumbnail(image.getIsThumbnail())
                .createdAt(image.getCreatedAt())
                .expireAt(image.getExpireAt())
                .build();
    }
}
