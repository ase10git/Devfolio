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

    List<PortfolioLike> findAllByUser(User user);

    // 특정 사용자가 특정 포트폴리오를 좋아요했는지 확인
    boolean existsByUserAndPortfolio(User user, Portfolio portfolio);

    // 특정 사용자가 특정 포트폴리오의 좋아요 삭제
    void deleteByUserAndPortfolio(User user, Portfolio portfolio);
    
    /**
     * 사용자와 포트폴리오로 좋아요 표시 조회
     */
    Optional<PortfolioLike> findByUserAndPortfolio(User user, Portfolio portfolio);
}
