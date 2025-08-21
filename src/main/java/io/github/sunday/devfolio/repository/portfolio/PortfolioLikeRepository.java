package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioLike;
import io.github.sunday.devfolio.entity.table.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioLikeRepository extends JpaRepository<PortfolioLike, Long> {

    /**
     * 사용자가 좋아요 표시한 포트폴리오 조회
     */
    List<PortfolioLike> findAllByUser_UserIdx(Long userIdx, Pageable pageable);

    /**
     * 사용자와 포트폴리오로 좋아요 표시 조회
     */
    Optional<PortfolioLike> findByUserAndPortfolio(User user, Portfolio portfolio);
}
