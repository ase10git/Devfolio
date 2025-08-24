package io.github.sunday.devfolio.controller.community;

import io.github.sunday.devfolio.config.CustomUserDetails;
import io.github.sunday.devfolio.dto.community.*;
import io.github.sunday.devfolio.entity.table.community.Category;
import io.github.sunday.devfolio.enums.community.CommunitySort;
import io.github.sunday.devfolio.service.community.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    /**
     * 커뮤니티 게시글 목록 및 검색 결과를 모두 처리하는 통합 메소드.
     * @GetMapping에 여러 경로를 지정하여 /community 와 /community/search 요청을
     * 이 메소드 하나가 모두 처리하도록 합니다.
     * URL이 바뀌지 않고 로직만 통일됩니다.
     */
    @GetMapping({"", "/search"}) // <-- 핵심 수정: {"", "/search"}
    public String showPosts(
            @Valid @ModelAttribute("requestDto") CommunitySearchRequestDto requestDto,
            BindingResult bindingResult,
            Model model
    ) {
        // (내부 로직은 searchPosts와 동일)
        List<String> errorMessages = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
        }

        // 서비스의 searchPosts 메소드를 호출합니다. (이 메소드는 검색 조건이 없으면 전체를 조회합니다)
        Page<PostListResponseDto> postPage = communityService.searchPosts(requestDto);

        model.addAttribute("postPage", postPage);
        model.addAttribute("requestDto", requestDto);
        model.addAttribute("categories", Category.values());
        model.addAttribute("sortOptions", CommunitySort.values());

        if (!errorMessages.isEmpty()) {
            model.addAttribute("error", errorMessages);
        }

        return "community/community_list";
    }

    /**
     * 게시글 상세 페이지를 표시합니다.
     */
    @GetMapping("/{postId}")
    public String detailPost(@PathVariable Long postId,
                             Model model,
                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Long currentUserId = (customUserDetails != null) ? customUserDetails.getUser().getUserIdx() : null;

        PostDetailResponseDto post = communityService.getPost(postId, currentUserId);
        model.addAttribute("post", post);
        model.addAttribute("commentRequest", new CommentCreateRequestDto());
        return "community/community_detail";
    }

    /**
     * 게시글 작성 폼 페이지를 표시합니다.
     */
    @GetMapping("/new")
    public String createPostForm(Model model) {
        model.addAttribute("postRequest", new PostCreateRequestDto());
        return "community/community_write";
    }

    /**
     * 새로운 게시글을 등록 처리합니다.
     */
    @PostMapping("/new")
    public String createPost(@Valid @ModelAttribute("postRequest") PostCreateRequestDto requestDto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if (bindingResult.hasErrors()) {
            return "community/community_write";
        }

        if (customUserDetails == null) {
            return "redirect:/login";
        }

        Long userId = customUserDetails.getUser().getUserIdx();
        Long newPostId = communityService.createPost(requestDto, userId);
        return "redirect:/community/" + newPostId;
    }

    /**
     * 게시글 수정 폼 페이지를 표시합니다.
     */
    @GetMapping("/{postId}/edit")
    public String editPostForm(@PathVariable Long postId,
                               Model model,
                               @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Long currentUserId = (customUserDetails != null) ? customUserDetails.getUser().getUserIdx() : null;

        PostDetailResponseDto post = communityService.getPost(postId, currentUserId);

        PostUpdateRequestDto requestDto = new PostUpdateRequestDto();
        requestDto.setPostIdx(post.getPostIdx());
        requestDto.setTitle(post.getTitle());
        requestDto.setContent(post.getContent());
        requestDto.setCategory(post.getCategory());
        requestDto.setStatus(post.getStatus());

        model.addAttribute("postRequest", requestDto);
        model.addAttribute("isEditMode", true);
        return "community/community_write";
    }

    /**
     * 게시글 수정을 처리합니다.
     */
    @PostMapping("/{postId}/edit")
    public String editPost(@PathVariable Long postId,
                           @Valid @ModelAttribute("postRequest") PostUpdateRequestDto requestDto,
                           BindingResult bindingResult,
                           @AuthenticationPrincipal CustomUserDetails customUserDetails,
                           Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEditMode", true);
            return "community/community_write";
        }

        if (customUserDetails == null) { return "redirect:/login"; }

        Long userId = customUserDetails.getUser().getUserIdx();
        requestDto.setPostIdx(postId);
        communityService.updatePost(requestDto, userId);
        return "redirect:/community/" + postId;
    }

    /**
     * 게시글 삭제를 처리합니다.
     */
    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable Long postId,
                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) { return "redirect:/login"; }

        Long userId = customUserDetails.getUser().getUserIdx();
        communityService.deletePost(postId, userId);
        return "redirect:/community";
    }

    /**
     * 새로운 댓글을 등록 처리합니다.
     */
    @PostMapping("/{postId}/comments")
    public String createComment(@PathVariable Long postId,
                                @ModelAttribute("commentRequest") CommentCreateRequestDto requestDto,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return "redirect:/login";
        }

        Long userId = customUserDetails.getUser().getUserIdx();

        requestDto.setPostId(postId);
        communityService.createComment(requestDto, userId);

        return "redirect:/community/" + postId;
    }

    /**
     * 게시글 좋아요를 처리합니다.
     */
    @PostMapping("/{postId}/like")
    public String toggleLike(@PathVariable Long postId,
                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return "redirect:/login";
        }

        Long userId = customUserDetails.getUser().getUserIdx();
        communityService.toggleLike(postId, userId);
        return "redirect:/community/" + postId;
    }

}