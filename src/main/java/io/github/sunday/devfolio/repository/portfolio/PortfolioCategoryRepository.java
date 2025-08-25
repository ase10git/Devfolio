package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.PortfolioCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 포트폴리오 카테고리 리포지토리
 */
public interface PortfolioCategoryRepository extends JpaRepository<PortfolioCategory, Long> {

    /**
     * 포트폴리오 카테고리 이름으로 조회
     */
    Optional<PortfolioCategory> findByName(String name);
}
