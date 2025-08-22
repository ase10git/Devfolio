package io.github.sunday.devfolio.controller.community;

import io.github.sunday.devfolio.config.CustomUserDetails;
import io.github.sunday.devfolio.dto.community.*;
import io.github.sunday.devfolio.entity.table.community.Category;
import io.github.sunday.devfolio.enums.CommunitySort;
import io.github.sunday.devfolio.enums.PortfolioSort;
import io.github.sunday.devfolio.service.community.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    /**
     * 커뮤니티 검색 요청이 들어올 때 DTO 내의 String에서 script를 제거
     */
    @InitBinder("searchRequestDto")
    public void initBinder(WebDataBinder binder) {
        // keyword와 category의 sanitize 수행
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true) {
            @Override
            public void setAsText(String text) {
                if (text != null) {
                    String safeText = Jsoup.clean(text, Safelist.basic());
                    super.setAsText(safeText.trim());
                } else {
                    super.setValue(null);
                }
            }
        });

        // sort 유효성 검증
        binder.registerCustomEditor(PortfolioSort.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                PortfolioSort sort = PortfolioSort.fromFieldName(text);
                if (sort == null) {
                    sort = PortfolioSort.UPDATED_AT;
                }
                setValue(sort);
            }
        });
    }

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
    public String listPosts(@Valid @ModelAttribute("searchRequestDto") CommunitySearchRequestDto requestDto,
                            Model model) {
        Page<PostListResponseDto> postPage = communityService.getPosts(requestDto);
        model.addAttribute("postPage", postPage);
        model.addAttribute("requestDto", requestDto);
        model.addAttribute("categories", Category.values());
        model.addAttribute("sortOptions", CommunitySort.values());
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
            @Valid @ModelAttribute("searchRequestDto") CommunitySearchRequestDto requestDto,
            BindingResult bindingResult,
            Model model
    ) {
        List<String> errorMessages = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList();
        }
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