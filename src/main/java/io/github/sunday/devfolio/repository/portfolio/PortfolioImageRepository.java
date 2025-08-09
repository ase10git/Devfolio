package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.PortfolioImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioImageRepository extends JpaRepository<PortfolioImage, Long> {
}
