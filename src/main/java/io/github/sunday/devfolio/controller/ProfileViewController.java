package io.github.sunday.devfolio.controller;

import io.github.sunday.devfolio.config.CustomUserDetails;
import io.github.sunday.devfolio.dto.ProfileDto;
import io.github.sunday.devfolio.dto.ProfileUpdateRequest;
import io.github.sunday.devfolio.entity.table.community.CommunityPost;
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
 * - GET /profile/{userIdx}?tab=resume|portfolio|posts|likes
 * - POST /profile/{userIdx}/follow
 * - POST /portfolios/{portfolioIdx}/like
 * - POST /posts/{postIdx}/like
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

    @GetMapping("/{userIdx}")
    public String viewProfile(@PathVariable Long userIdx,
                              @RequestParam(defaultValue = "resume") String tab,
                              @AuthenticationPrincipal CustomUserDetails currentUserDetails,
                              @RequestParam(required = false) String verifyError,
                              Model model) {

        User currentUser = (currentUserDetails != null) ? currentUserDetails.getUser() : null;

        ProfileDto dto = profileService.getProfile(userIdx, currentUser);
        boolean isSelf = currentUser != null && userIdx.equals(currentUser.getUserIdx());

        List<Resume> resumes = Collections.emptyList();
        List<Portfolio> portfolios = Collections.emptyList();
        List<CommunityPost> posts = Collections.emptyList();
        List<Portfolio> likedPortfolios = Collections.emptyList();
        List<CommunityPost> likedPosts = Collections.emptyList();
        List<User> followers = Collections.emptyList();
        List<User> following = Collections.emptyList();

        Set<Long> likedPortfolioIds = new HashSet<>();
        Set<Long> likedPostIds = new HashSet<>();
        Set<Long> myFollowingIds = profileService.getFollowingUserIds(currentUser);

        switch (tab) {
            case "portfolio":
                portfolios = profileService.getPortfolios(userIdx);
                if (currentUser != null) {
                    likedPortfolios = profileService.getLikedPortfolios(currentUser);
                    likedPortfolios.forEach(p -> likedPortfolioIds.add(p.getPortfolioIdx()));
                }
                break;
            case "posts":
                posts = profileService.getPosts(userIdx);
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
                    return "redirect:/profile/" + userIdx + "?tab=resume";
                }
                break;
            case "followers":
                followers = profileService.getFollowers(userIdx);
                break;
            case "following":
                following = profileService.getFollowing(userIdx);
                break;
            case "resume":
            default:
                resumes = profileService.getResumes(userIdx);
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

        return "profile/profile";
    }

    /** 팔로우/언팔로우 (리다이렉트 목적지 유지용 ownerUserIdx, tab 사용) */
    @PostMapping("/{targetUserIdx}/follow")
    public String toggleFollow(@PathVariable Long targetUserIdx,
                               @RequestParam Long ownerUserIdx,
                               @RequestParam(defaultValue = "resume") String tab,
                               @AuthenticationPrincipal CustomUserDetails currentUserDetails) {
        User currentUser = (currentUserDetails != null) ? currentUserDetails.getUser() : null;
        profileService.toggleFollow(currentUser, targetUserIdx);
        return "redirect:/profile/" + ownerUserIdx + "?tab=" + tab;
    }

    @PostMapping("/portfolios/{portfolioIdx}/like")
    public String togglePortfolioLike(@PathVariable Long portfolioIdx,
                                      @RequestParam Long ownerUserIdx,
                                      @RequestParam(defaultValue = "portfolio") String tab,
                                      @AuthenticationPrincipal CustomUserDetails currentUserDetails) {
        User currentUser = (currentUserDetails != null) ? currentUserDetails.getUser() : null;
        profileService.togglePortfolioLike(currentUser, portfolioIdx);
        return "redirect:/profile/" + ownerUserIdx + "?tab=" + tab;
    }

    @PostMapping("/posts/{postIdx}/like")
    public String togglePostLike(@PathVariable Long postIdx,
                                 @RequestParam Long ownerUserIdx,
                                 @RequestParam(defaultValue = "posts") String tab,
                                 @AuthenticationPrincipal CustomUserDetails currentUserDetails) {
        User currentUser = (currentUserDetails != null) ? currentUserDetails.getUser() : null;
        profileService.togglePostLike(currentUser, postIdx);
        return "redirect:/profile/" + ownerUserIdx + "?tab=" + tab;
    }

    // ----- 비밀번호 재확인 모달 처리 → 성공 시 수정 페이지로, 실패 시 에러 플래그 -----

    @PostMapping("/verify")
    public String verifyPassword(@RequestParam String password,
                                 @AuthenticationPrincipal CustomUserDetails currentUserDetails) {
        User currentUser = (currentUserDetails != null) ? currentUserDetails.getUser() : null;
        try {
            updateService.verifyPassword(currentUser, password);
            return "redirect:/profile/" + currentUser.getUserIdx() + "/edit";
        } catch (BadCredentialsException e) {
            return "redirect:/profile/" + currentUser.getUserIdx() + "?tab=resume&verifyError=1";
        }
    }

    // ----- 수정 페이지 -----

    @GetMapping("/{userIdx}/edit")
    public String editForm(@PathVariable Long userIdx,
                           @AuthenticationPrincipal CustomUserDetails currentUserDetails,
                           Model model) {
        User currentUser = (currentUserDetails != null) ? currentUserDetails.getUser() : null;
        if (currentUser == null || !userIdx.equals(currentUser.getUserIdx())) {
            return "redirect:/profile/" + userIdx;
        }
        ProfileUpdateRequest form = new ProfileUpdateRequest();
        form.setEmail(currentUser.getEmail());
        form.setNickname(currentUser.getNickname());
        form.setGithubUrl(currentUser.getGithubUrl());
        form.setBlogUrl(currentUser.getBlogUrl());
        form.setAffiliation(currentUser.getAffiliation());
        form.setProfileImg(currentUser.getProfileImg());

        model.addAttribute("form", form);
        model.addAttribute("userIdx", userIdx);
        return "profile/profile_edit";
    }

    @PostMapping("/{userIdx}/edit")
    public String editSubmit(@PathVariable Long userIdx,
                             @AuthenticationPrincipal CustomUserDetails currentUserDetails,
                             @Valid @ModelAttribute("form") ProfileUpdateRequest form,
                             Model model) throws BindException {
        User currentUser = (currentUserDetails != null) ? currentUserDetails.getUser() : null;
        if (currentUser == null || !userIdx.equals(currentUser.getUserIdx())) {
            return "redirect:/profile/" + userIdx;
        }
        try {
            updateService.updateProfile(currentUser, form);
            return "redirect:/profile/" + userIdx + "?tab=resume";
        } catch (IllegalArgumentException e) {
            model.addAttribute("form", form);
            model.addAttribute("userIdx", userIdx);
            model.addAttribute("error", e.getMessage());
            return "profile/profile_edit";
        }
    }
}