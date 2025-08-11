package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 포트폴리오 이미지 리포지토리 
 */
public interface PortfolioImageRepository extends JpaRepository<PortfolioImage, Long> {

    /**
     * 포트폴리오로 이미지 찾기
     */
    Optional<PortfolioImage> findFirst1ByPortfolio(Portfolio portfolio);
}
