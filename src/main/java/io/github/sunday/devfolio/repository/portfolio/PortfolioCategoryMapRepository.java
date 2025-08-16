package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioCategoryMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 포트폴리오와 포트폴리오 카테고리 매핑 리포지토리
 */
public interface PortfolioCategoryMapRepository extends JpaRepository<PortfolioCategoryMap, Long> {

    /**
     * 포트폴리오로 매핑된 관계 조회 
     */
    List<PortfolioCategoryMap> findAllByPortfolio(Portfolio portfolio);

    /**
     * 포트폴리오 매핑 관계 제거
     */
    void deleteByPortfolioAndPortfolioCategory_CategoryIdx(Portfolio portfolio, Long categoryIdx);
}
