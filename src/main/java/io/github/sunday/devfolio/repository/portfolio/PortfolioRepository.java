package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 포트폴리오 리포지토리
 */
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    /**
     * 포트폴리오 전체 조회
     */
    Page<Portfolio> findAll(Pageable pageable);
}
