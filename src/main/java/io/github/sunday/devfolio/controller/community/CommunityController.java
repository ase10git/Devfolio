package io.github.sunday.devfolio.controller.community;

import io.github.sunday.devfolio.config.CustomUserDetails;
import io.github.sunday.devfolio.dto.community.*;
import io.github.sunday.devfolio.service.community.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;


    /**
     * 포트폴리오 작성 요청이 들어올 때 DTO 내의 String에서 script를 제거
     */
    @InitBinder({"writeRequestDto", "editRequestDto"})
    public void initBinderToWrite(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true) {
            @Override
            public void setAsText(String text) {
                if (text != null) {
                    String safeText = Jsoup.clean(text, Safelist.relaxed());
                    super.setAsText(safeText.trim());
                } else {
                    super.setValue(null);
                }
            }
        });
    }

    /**
     * 커뮤니티 게시글 목록 페이지를 표시합니다.
     */
    @GetMapping
    public String listPosts(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                            Model model) {
        Page<PostListResponseDto> postPage = communityService.getPosts(pageable);
        model.addAttribute("postPage", postPage);
        return "community/community_list";
    }

    /**
     * 커뮤니티 게시글 목록 페이지를 표시합니다.
     *
     * @param model    뷰에 전달할 데이터 모델
     * @return 뷰 템플릿 경로
     */
    @GetMapping("/search")
    public String searchPosts(
            @Valid @ModelAttribute CommunitySearchRequestDto searchRequestDto,
            Model model) {
        Page<PostListResponseDto> postPage = communityService.searchPosts(searchRequestDto);
        model.addAttribute("searchDto", searchRequestDto);
        model.addAttribute("postPage", postPage);
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
    public String createPost(@ModelAttribute("postRequest") PostCreateRequestDto requestDto,
                             @AuthenticationPrincipal CustomUserDetails customUserDetails) {

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
                           @ModelAttribute("postRequest") PostUpdateRequestDto requestDto,
                           @AuthenticationPrincipal CustomUserDetails customUserDetails) {
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