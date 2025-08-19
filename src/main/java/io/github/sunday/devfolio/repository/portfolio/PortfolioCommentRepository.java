package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.PortfolioComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioCommentRepository extends JpaRepository<PortfolioComment, Long> {
}
