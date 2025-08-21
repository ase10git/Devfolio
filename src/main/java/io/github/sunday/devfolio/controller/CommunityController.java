package io.github.sunday.devfolio.controller;

import io.github.sunday.devfolio.dto.community.*;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.service.community.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    /**
     * 커뮤니티 게시글 목록 페이지를 표시합니다.
     *
     * @param model    뷰에 전달할 데이터 모델
     * @return 뷰 템플릿 경로
     */
    @GetMapping
    public String listPosts(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                            Model model) {
        Page<PostListResponse> postPage = communityService.getPosts(pageable);
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
        Page<PostListResponse> postPage = communityService.searchPosts(searchRequestDto);
        model.addAttribute("searchDto", searchRequestDto);
        model.addAttribute("postPage", postPage);
        return "community/community_list";
    }

    /**
     * 게시글 상세 페이지를 표시합니다.
     *
     * @param postId 조회할 게시글의 ID
     * @param model  뷰에 전달할 데이터 모델
     * @return 뷰 템플릿 경로
     */
    @GetMapping("/{postId}")
    public String detailPost(@PathVariable Long postId,
                             Model model,
                             @AuthenticationPrincipal User user) { // 현재 사용자 정보 받기
        PostDetailResponse post = communityService.getPost(postId, user); // user 정보 전달
        model.addAttribute("post", post);
        model.addAttribute("commentRequest", new CommentCreateRequest());
        return "community_detail";
    }

    /**
     * 게시글 작성 폼 페이지를 표시합니다.
     *
     * @param model 뷰에 전달할 데이터 모델
     * @return 뷰 템플릿 경로
     */
    @GetMapping("/new")
    public String createPostForm(Model model) {
        model.addAttribute("postRequest", new PostCreateRequest());
        return "community_write";
    }

//    /**
//     * 새로운 게시글을 등록 처리합니다.
//     *
//     * @param requestDto      폼에서 전송된 게시글 데이터
//     * @param user            현재 인증된 사용자 정보 (Spring Security가 주입)
//     * @return 처리 후 리다이렉트할 URL
//     */
//    @PostMapping("/new")
//    public String createPost(@ModelAttribute("postRequest") PostCreateRequest requestDto,
//                             @AuthenticationPrincipal User user) {
//        if (user == null) {
//            return "redirect:/login"; // 예시: 로그인 페이지로 리다이렉트
//        }
//
//        Long newPostId = communityService.createPost(requestDto, user);
//        return "redirect:/community/" + newPostId; // 방금 작성한 글로 리다이렉트
//    }

    @PostMapping("/new")
    public String createPost(@ModelAttribute("postRequest") PostCreateRequest requestDto) { // @AuthenticationPrincipal 제거
        // [핵심 수정] 임시 User 객체 생성
        // 로그인 기능 구현 전까지 항상 user_idx=1인 사용자가 글을 작성하는 것으로 가정
        User tempUser = User.builder().userIdx(1L).build();

        Long newPostId = communityService.createPost(requestDto, tempUser);
        return "redirect:/community/" + newPostId;
    }

    /**
     * 게시글 수정 폼 페이지를 표시합니다.
     *
     * @param postId 수정할 게시글의 ID
     * @param model  뷰에 전달할 데이터 모델
     * @return 뷰 템플릿 경로
     */
    @GetMapping("/{postId}/edit")
    public String editPostForm(@PathVariable Long postId,
                               Model model,
                               @AuthenticationPrincipal User user) { // 1. 현재 사용자 정보 받기
        // 2. getPost 호출 시 user 정보 함께 전달
        PostDetailResponse post = communityService.getPost(postId, user);

        // PostDetailResponse를 PostUpdateRequest로 변환하여 모델에 추가
        PostUpdateRequest requestDto = new PostUpdateRequest();
        requestDto.setPostIdx(post.getPostIdx());
        requestDto.setTitle(post.getTitle());
        requestDto.setContent(post.getContent());

        model.addAttribute("postRequest", requestDto);
        model.addAttribute("isEditMode", true);
        return "community_write";
    }

    /**
     * 게시글 수정을 처리합니다.
     *
     * @param postId      수정할 게시글의 ID
     * @param requestDto  폼에서 전송된 수정 데이터
     * @param user        현재 인증된 사용자
     * @return 처리 후 리다이렉트할 URL
     */
    @PostMapping("/{postId}/edit")
    public String editPost(@PathVariable Long postId,
                           @ModelAttribute("postRequest") PostUpdateRequest requestDto,
                           @AuthenticationPrincipal User user) {
        requestDto.setPostIdx(postId); // URL의 postId를 DTO에 설정
        communityService.updatePost(requestDto, user);
        return "redirect:/community/" + postId;
    }

    /**
     * 게시글 삭제를 처리합니다.
     *
     * @param postId 삭제할 게시글의 ID
     * @param user   현재 인증된 사용자
     * @return 처리 후 리다이렉트할 URL
     */
    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        communityService.deletePost(postId, user);
        return "redirect:/community";
    }

    /**
     * 새로운 댓글을 등록 처리합니다.
     *
     * @param postId      댓글을 등록할 게시글의 ID
     * @param requestDto  폼에서 전송된 댓글 데이터
     * @param user        현재 인증된 사용자
     * @return 처리 후 해당 게시글 상세 페이지로 리다이렉트
     */
    @PostMapping("/{postId}/comments")
    public String createComment(@PathVariable Long postId,
                                @ModelAttribute("commentRequest") CommentCreateRequest requestDto,
                                @AuthenticationPrincipal User user) {
        // 로그인하지 않은 사용자는 SecurityConfig에서 차단하는 것이 이상적
        if (user == null) {
            return "redirect:/login";
        }

        requestDto.setPostId(postId); // URL의 postId를 DTO에 설정
        communityService.createComment(requestDto, user);

        return "redirect:/community/" + postId;
    }

    /**
     * 게시글 좋아요를 처리합니다.
     *
     * @param postId 좋아요를 처리할 게시글의 ID
     * @param user   현재 인증된 사용자
     * @return 처리 후 해당 게시글 상세 페이지로 리다이렉트
     */
    @PostMapping("/{postId}/like")
    public String toggleLike(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        if (user == null) {
            return "redirect:/login"; // 비로그인 사용자는 로그인 페이지로
        }
        communityService.toggleLike(postId, user);
        return "redirect:/community/" + postId;
    }
}