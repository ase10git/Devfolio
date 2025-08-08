package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.PortfolioCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioCategoryRepository extends JpaRepository<PortfolioCategory, Long> {
}
