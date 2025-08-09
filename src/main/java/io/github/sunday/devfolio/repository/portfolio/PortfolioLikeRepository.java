package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.PortfolioLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioLikeRepository extends JpaRepository<PortfolioLike, Long> {
}
