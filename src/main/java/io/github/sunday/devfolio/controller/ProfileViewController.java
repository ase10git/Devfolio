package io.github.sunday.devfolio.controller;

import io.github.sunday.devfolio.dto.ProfileDto;
import io.github.sunday.devfolio.dto.ProfileUpdateRequest;
import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.profile.Resume;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.service.ProfileService;
import io.github.sunday.devfolio.service.ProfileUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.BindException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 프로필 페이지 렌더링 및 팔로우/좋아요 토글을 처리하는 MVC 컨트롤러.
 * - GET /profile/{userId}?tab=resume|portfolio|posts|likes
 * - POST /profile/{userId}/follow
 * - POST /portfolios/{portfolioId}/like
 * - POST /posts/{postId}/like
 *
 * 주의:
 * - "좋아요" 탭은 본인 프로필에서만 노출/접근.
 * - CSRF 활성화 시 폼에 토큰 포함 필요(템플릿에 반영됨).
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileViewController {

    private final ProfileService profileService;
    private final ProfileUpdateService updateService;

    @GetMapping("/{userId}")
    public String viewProfile(@PathVariable Long userId,
                              @RequestParam(defaultValue = "resume") String tab,
                              @AuthenticationPrincipal User currentUser,
                              @RequestParam(required = false) String verifyError,
                              Model model) {

        ProfileDto dto = profileService.getProfile(userId, currentUser);
        boolean isSelf = currentUser != null && userId.equals(currentUser.getUserIdx());

        List<Resume> resumes = Collections.emptyList();
        List<Portfolio> portfolios = Collections.emptyList();
        List<Post> posts = Collections.emptyList();
        List<Portfolio> likedPortfolios = Collections.emptyList();
        List<Post> likedPosts = Collections.emptyList();
        List<User> followers = Collections.emptyList();
        List<User> following = Collections.emptyList();

        Set<Long> likedPortfolioIds = new HashSet<>();
        Set<Long> likedPostIds = new HashSet<>();
        Set<Long> myFollowingIds = profileService.getFollowingUserIds(currentUser);

        switch (tab) {
            case "portfolio":
                portfolios = profileService.getPortfolios(userId);
                if (currentUser != null) {
                    likedPortfolios = profileService.getLikedPortfolios(currentUser);
                    likedPortfolios.forEach(p -> likedPortfolioIds.add(p.getPortfolioIdx()));
                }
                break;
            case "posts":
                posts = profileService.getPosts(userId);
                if (currentUser != null) {
                    likedPosts = profileService.getLikedPosts(currentUser);
                    likedPosts.forEach(p -> likedPostIds.add(p.getPostIdx()));
                }
                break;
            case "likes":
                if (isSelf && currentUser != null) {
                    likedPortfolios = profileService.getLikedPortfolios(currentUser);
                    likedPosts = profileService.getLikedPosts(currentUser);
                } else {
                    return "redirect:/profile/" + userId + "?tab=resume";
                }
                break;
            case "followers":
                followers = profileService.getFollowers(userId);
                break;
            case "following":
                following = profileService.getFollowing(userId);
                break;
            case "resume":
            default:
                resumes = profileService.getResumes(userId);
        }

        model.addAttribute("user", dto);
        model.addAttribute("tab", tab);
        model.addAttribute("isSelf", isSelf);
        model.addAttribute("isFollowing", dto.isFollowing());
        model.addAttribute("verifyError", verifyError);

        model.addAttribute("resumes", resumes);
        model.addAttribute("portfolios", portfolios);
        model.addAttribute("posts", posts);
        model.addAttribute("likesPortfolios", likedPortfolios);
        model.addAttribute("likesPosts", likedPosts);
        model.addAttribute("followers", followers);
        model.addAttribute("following", following);

        model.addAttribute("likedPortfolioIds", likedPortfolioIds);
        model.addAttribute("likedPostIds", likedPostIds);
        model.addAttribute("myFollowingIds", myFollowingIds);

        return "profile";
    }

    /** 팔로우/언팔로우 (리다이렉트 목적지 유지용 ownerUserId, tab 사용) */
    @PostMapping("/{targetUserId}/follow")
    public String toggleFollow(@PathVariable Long targetUserId,
                               @RequestParam Long ownerUserId,
                               @RequestParam(defaultValue = "resume") String tab,
                               @AuthenticationPrincipal User currentUser) {
        profileService.toggleFollow(currentUser, targetUserId);
        return "redirect:/profile/" + ownerUserId + "?tab=" + tab;
    }

    @PostMapping("/portfolios/{portfolioId}/like")
    public String togglePortfolioLike(@PathVariable Long portfolioId,
                                      @RequestParam Long ownerUserId,
                                      @RequestParam(defaultValue = "portfolio") String tab,
                                      @AuthenticationPrincipal User currentUser) {
        profileService.togglePortfolioLike(currentUser, portfolioId);
        return "redirect:/profile/" + ownerUserId + "?tab=" + tab;
    }

    @PostMapping("/posts/{postId}/like")
    public String togglePostLike(@PathVariable Long postId,
                                 @RequestParam Long ownerUserId,
                                 @RequestParam(defaultValue = "posts") String tab,
                                 @AuthenticationPrincipal User currentUser) {
        profileService.togglePostLike(currentUser, postId);
        return "redirect:/profile/" + ownerUserId + "?tab=" + tab;
    }

    // ----- 비밀번호 재확인 모달 처리 → 성공 시 수정 페이지로, 실패 시 에러 플래그 -----

    @PostMapping("/verify")
    public String verifyPassword(@RequestParam String password,
                                 @AuthenticationPrincipal User currentUser) {
        try {
            updateService.verifyPassword(currentUser, password);
            return "redirect:/profile/" + currentUser.getUserIdx() + "/edit";
        } catch (BadCredentialsException e) {
            return "redirect:/profile/" + currentUser.getUserIdx() + "?tab=resume&verifyError=1";
        }
    }

    // ----- 수정 페이지 -----

    @GetMapping("/{userId}/edit")
    public String editForm(@PathVariable Long userId,
                           @AuthenticationPrincipal User currentUser,
                           Model model) {
        if (currentUser == null || !userId.equals(currentUser.getUserIdx())) {
            return "redirect:/profile/" + userId;
        }
        ProfileUpdateRequest form = new ProfileUpdateRequest();
        form.setEmail(currentUser.getEmail());
        form.setNickname(currentUser.getNickname());
        form.setGithubUrl(currentUser.getGithubUrl());
        form.setBlogUrl(currentUser.getBlogUrl());
        form.setAffiliation(currentUser.getAffiliation());
        form.setProfileImg(currentUser.getProfileImg());

        model.addAttribute("form", form);
        model.addAttribute("userId", userId);
        return "profile_edit";
    }

    @PostMapping("/{userId}/edit")
    public String editSubmit(@PathVariable Long userId,
                             @AuthenticationPrincipal User currentUser,
                             @Valid @ModelAttribute("form") ProfileUpdateRequest form,
                             Model model) throws BindException {
        if (currentUser == null || !userId.equals(currentUser.getUserIdx())) {
            return "redirect:/profile/" + userId;
        }
        try {
            updateService.updateProfile(currentUser, form);
            return "redirect:/profile/" + userId + "?tab=resume";
        } catch (IllegalArgumentException e) {
            model.addAttribute("form", form);
            model.addAttribute("userId", userId);
            model.addAttribute("error", e.getMessage());
            return "profile_edit";
        }
    }
}