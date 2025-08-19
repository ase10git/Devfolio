package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 포트폴리오 이미지 리포지토리 
 */
public interface PortfolioImageRepository extends JpaRepository<PortfolioImage, Long> {

    /**
     * 포트폴리오로 썸네일용 이미지 찾기
     * 썸네일 여부를 확인하는 컬럼이 True인 이미지를 선택
     */
    Optional<PortfolioImage> findByPortfolio_PortfolioIdxAndIsThumbnailTrue(Long portfolioIdx);

    /**
     * 포트폴리오의 모든 이미지 찾기
     */
    List<PortfolioImage> findAllByPortfolio_PortfolioIdx(Long portfolioIdx);
}
