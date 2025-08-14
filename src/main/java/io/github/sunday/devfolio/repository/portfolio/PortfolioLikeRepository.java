package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.PortfolioLike;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioLikeRepository extends JpaRepository<PortfolioLike, Long> {

    /**
     * 사용자가 좋아요 표시한 포트폴리오 조회
     */
    List<PortfolioLike> findAllByUser_UserIdx(Long userIdx, Pageable pageable);
}
