package io.github.sunday.devfolio.service;

import io.github.sunday.devfolio.dto.ProfileDto;
import io.github.sunday.devfolio.entity.table.community.CommunityLike;
import io.github.sunday.devfolio.entity.table.community.CommunityPost;
import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioLike;
import io.github.sunday.devfolio.entity.table.profile.Follow;
import io.github.sunday.devfolio.entity.table.profile.Resume;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.FollowRepository;
import io.github.sunday.devfolio.repository.ResumeRepository;
import io.github.sunday.devfolio.repository.UserRepository;
import io.github.sunday.devfolio.repository.community.CommunityLikeRepository;
import io.github.sunday.devfolio.repository.community.CommunityPostRepository;
import io.github.sunday.devfolio.repository.portfolio.PortfolioLikeRepository;
import io.github.sunday.devfolio.repository.portfolio.PortfolioRepository;
import io.github.sunday.devfolio.service.portfolio.PortfolioLikeService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 프로필 화면에 필요한 도메인 데이터를 조회/조작하는 서비스.
 *
 * 주요 책임
 *
 * 특정 사용자의 프로필 정보(팔로워/팔로잉 수, 팔로우 상태 포함) 조회
 * 팔로우/언팔로우 토글
 * 프로필 탭(이력서 등) 컨텐츠 조회
 */
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepo;
    private final FollowRepository followRepo;
    private final ResumeRepository resumeRepo;
    private final PortfolioRepository portfolioRepo;
    private final CommunityPostRepository postRepo;
    private final PortfolioLikeRepository portfolioLikeRepo;
    private final CommunityLikeRepository CommunityLikeRepo;
    private final PortfolioLikeService portfolioLikeService;

    // -------- 요약/팔로우/이력서 --------

    @Transactional(readOnly = true)
    public ProfileDto getProfile(Long targetUserIdx, User currentUser) {
        User target = userRepo.findByUserIdx(targetUserIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        long followingCount = followRepo.countByFollower(target);
        long followerCount  = followRepo.countByFollowed(target);
        boolean isFollowing = currentUser != null &&
                followRepo.existsByFollowerAndFollowed(currentUser, target);

        return ProfileDto.builder()
                .userIdx(target.getUserIdx())
                .nickname(target.getNickname())
                .email(target.getEmail())
                .oauthProvider(target.getOauthProvider())
                .profileImg(target.getProfileImg())
                .githubUrl(target.getGithubUrl())
                .blogUrl(target.getBlogUrl())
                .affiliation(target.getAffiliation())
                .followingCount(followingCount)
                .followerCount(followerCount)
                .isFollowing(isFollowing)
                .build();
    }

    @Transactional
    public boolean toggleFollow(User currentUser, Long targetUserIdx) {
        if (currentUser == null) throw new IllegalStateException("로그인이 필요합니다");
        User target = userRepo.findByUserIdx(targetUserIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        if (followRepo.existsByFollowerAndFollowed(currentUser, target)) {
            followRepo.deleteByFollowerAndFollowed(currentUser, target);
            return false;
        } else {
            followRepo.save(Follow.builder()
                    .follower(currentUser)
                    .followed(target)
                    .followedAt(ZonedDateTime.now(ZoneId.of("UTC")))
                    .build());
            return true;
        }
    }

    @Transactional(readOnly = true)
    public List<Resume> getResumes(Long targetUserIdx) {
        User target = userRepo.findByUserIdx(targetUserIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
        return resumeRepo.findAllByUser(target);
    }

    // -------- 포트폴리오/게시글 --------

    @Transactional(readOnly = true)
    public List<Portfolio> getPortfolios(Long targetUserIdx) {
        User target = userRepo.findByUserIdx(targetUserIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
        return portfolioRepo.findAllByUser(target);
    }

    @Transactional(readOnly = true)
    public List<CommunityPost> getPosts(Long targetUserIdx) {
        User target = userRepo.findByUserIdx(targetUserIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
        return postRepo.findAllByUser(target);
    }

    // -------- 좋아요 조회/토글 --------

    @Transactional(readOnly = true)
    public List<Portfolio> getLikedPortfolios(User currentUser) {
        if (currentUser == null) throw new IllegalStateException("로그인이 필요합니다");
        return portfolioLikeRepo.findAllByUser(currentUser)
                .stream().map(PortfolioLike::getPortfolio).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommunityPost> getLikedPosts(User currentUser) {
        if (currentUser == null) throw new IllegalStateException("로그인이 필요합니다");
        return CommunityLikeRepo.findAllByUser(currentUser)
                .stream().map(CommunityLike::getPost).collect(Collectors.toList());
    }

    @Transactional
    public void togglePortfolioLike(User currentUser, Long portfolioIdx) throws Exception {
        if (currentUser == null) throw new IllegalStateException("로그인이 필요합니다");
        Boolean isLiked = portfolioLikeService.userLikedPortfolio(currentUser.getUserIdx(), portfolioIdx);

        if (isLiked) {
            portfolioLikeService.removeLike(portfolioIdx, currentUser.getUserIdx());
        } else {
            portfolioLikeService.addLike(portfolioIdx, currentUser.getUserIdx());
        }
    }

    @Transactional
    public boolean togglePostLike(User currentUser, Long postIdx) {
        if (currentUser == null) throw new IllegalStateException("로그인이 필요합니다");
        CommunityPost post = postRepo.findById(postIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글"));
        if (CommunityLikeRepo.existsByUserAndPost(currentUser, post)) {
            CommunityLikeRepo.deleteByUserAndPost(currentUser, post);
            return false;
        }
        CommunityLikeRepo.save(CommunityLike.builder()
                .user(currentUser)
                .post(post)
                .likedAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .build());
        return true;
    }

    // -------- 팔로워/팔로잉 목록 + 현재 사용자의 팔로잉 집합 --------

    @Transactional(readOnly = true)
    public List<User> getFollowers(Long targetUserIdx) {
        User target = userRepo.findByUserIdx(targetUserIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
        return followRepo.findAllByFollowed(target)
                .stream().map(Follow::getFollower).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<User> getFollowing(Long targetUserIdx) {
        User target = userRepo.findByUserIdx(targetUserIdx)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
        return followRepo.findAllByFollower(target)
                .stream().map(Follow::getFollowed).collect(Collectors.toList());
    }

    /** 현재 로그인 사용자가 팔로우(팔로잉) 중인 사용자 id 집합 */
    @Transactional(readOnly = true)
    public Set<Long> getFollowingUserIds(User currentUser) {
        if (currentUser == null) return Set.of();
        return followRepo.findAllByFollower(currentUser).stream()
                .map(f -> f.getFollowed().getUserIdx())
                .collect(Collectors.toSet());
    }
}
