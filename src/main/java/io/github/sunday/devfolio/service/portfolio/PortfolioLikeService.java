package io.github.sunday.devfolio.service.portfolio;

import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioLike;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.portfolio.PortfolioLikeRepository;
import io.github.sunday.devfolio.repository.portfolio.PortfolioRepository;
import io.github.sunday.devfolio.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * 포트폴리오의 좋아요 표시를 관리하는 서비스
 */
@Service
@RequiredArgsConstructor
public class PortfolioLikeService {
    private final PortfolioLikeRepository portfolioLikeRepository;
    private final PortfolioRepository portfolioRepository;
    private final UserServiceImpl userService;

    /**
     * 특정 포트폴리오와 특정 사용자의 좋아요 조회
     */
    public Boolean userLikedPortfolio(Long userIdx, Long portfolioIdx) throws Exception {
        Portfolio portfolio = validatePortfolio(portfolioIdx);
        User user = validateUser(userIdx);
        
        // 작성자 검증
        validateNotOwner(user, portfolio);

        PortfolioLike original = portfolioLikeRepository.findByUserAndPortfolio(user, portfolio).orElse(null);
        return (original != null);
    }

    /**
     * 포트폴리오에 좋아요 추가
     */
    public void addLike(Long portfolioIdx, Long userIdx) throws Exception {
        Portfolio portfolio = validatePortfolio(portfolioIdx);
        User user = validateUser(userIdx);

        // 작성자 검증
        validateNotOwner(user, portfolio);

        PortfolioLike original = portfolioLikeRepository.findByUserAndPortfolio(user, portfolio).orElse(null);
        if (original != null) {
            throw new Exception("이미 좋아요를 등록했습니다");
        }

        PortfolioLike portfolioLike = PortfolioLike.builder()
                .portfolio(portfolio)
                .user(user)
                .likedAt(ZonedDateTime.now())
                .build();
        PortfolioLike saved = portfolioLikeRepository.save(portfolioLike);

        if (saved == null) {
            throw new Exception("좋아요 추가 중에 에러가 발생했습니다");
        }
    }

    /**
     * 포트폴리오에 좋아요 제거
     */
    public void removeLike(Long portfolioIdx, Long userIdx) throws Exception {
        Portfolio portfolio = validatePortfolio(portfolioIdx);
        User user = validateUser(userIdx);

        // 작성자 검증
        validateNotOwner(user, portfolio);

        PortfolioLike target = portfolioLikeRepository.findByUserAndPortfolio(user, portfolio).orElse(null);

        if (target != null) {
            portfolioLikeRepository.delete(target);
        } else {
            throw new Exception("존재하지 않거나 이미 제거된 좋아요 표시입니다");
        }
    }

    /**
     * 포트폴리오와 사용자 조회 및 검증
     */
    private Portfolio validatePortfolio(Long portfolioIdx) throws Exception {
        return portfolioRepository.findById(portfolioIdx)
                .orElseThrow(() -> new Exception("포트폴리오가 존재하지 않습니다"));
    }

    private User validateUser(Long userIdx) throws Exception {
        User user = userService.findByUserIdx(userIdx);
        if (user == null) {
            throw new Exception("사용자가 존재하지 않습니다");
        }
        return user;
    }

    private void validateNotOwner(User user, Portfolio portfolio) throws Exception {
        if (user.getUserIdx().equals(portfolio.getUser().getUserIdx())) {
            throw new Exception("접근이 제한되었습니다");
        }
    }
}
