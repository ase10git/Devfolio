package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

}
