package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.PortfolioComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 포트폴리오 댓글 리포지토리
 */
public interface PortfolioCommentRepository extends JpaRepository<PortfolioComment, Long> {

    /**
     * 포트폴리오 IDX로 모든 댓글 조회
     */
    List<PortfolioComment> findAllByPortfolio_PortfolioIdx(Long portfolioIdx);
}
