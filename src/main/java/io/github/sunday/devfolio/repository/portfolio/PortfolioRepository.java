package io.github.sunday.devfolio.repository.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.profile.Resume;
import io.github.sunday.devfolio.entity.table.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 포트폴리오 리포지토리
 */
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    /**
     * 핫한 포트폴리오 조회
     * 현재 날짜 기준으로 일주일 간 업로드된 포트폴리오 중 좋아요, 조회수 내림차순으로 상위 5개 선정
     */
    List<Portfolio> findTop5ByCreatedAtBetweenOrderByLikeCountDescViewsDesc(ZonedDateTime startDate, ZonedDateTime endDate);

    /**
     * 인기 포트폴리오 조회
     * 업로드된 포트폴리오 중 좋아요, 조회수 내림차순으로 상위 5개 선정
     */
    List<Portfolio> findTop5ByOrderByLikeCountDescViewsDescCreatedAtDesc();

    /**
     * 사용자 포트폴리오 조회
     * 사용자의 user_idx 값으로 포트폴리오 조회
     */
    List<Portfolio> findAllByUser_UserIdx(Long userIdx, Pageable pageable);

    List<Portfolio> findAllByUser(User user);
}
