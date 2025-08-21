package io.github.sunday.devfolio.service.community;

import io.github.sunday.devfolio.dto.community.*;
import io.github.sunday.devfolio.entity.table.community.CommunityComment;
import io.github.sunday.devfolio.entity.table.community.CommunityLike;
import io.github.sunday.devfolio.entity.table.community.CommunityPost;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.UserRepository;
import io.github.sunday.devfolio.repository.community.CommunityCommentRepository;
import io.github.sunday.devfolio.repository.community.CommunityLikeRepository;
import io.github.sunday.devfolio.repository.community.CommunityPostRepository;
import io.github.sunday.devfolio.repository.community.CommunityQueryDslRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * 커뮤니티 게시판 관련 비즈니스 로직을 처리하는 서비스 클래스.
 *
 * @author YourName
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityService {

    private final CommunityPostRepository communityPostRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final UserRepository userRepository;
    private final CommunityQueryDslRepository communityQueryDslRepository;

    /**
     * 게시글 목록을 페이징하여 조회합니다.
     *
     * @param pageable 페이징 및 정렬 정보
     * @return 페이징 처리된 게시글 목록 (PostListResponse)
     */
    public Page<PostListResponse> getPosts(Pageable pageable) {
        return communityPostRepository.findPostsWithCommentCount(pageable);
    }

    /**
     * 게시글을 검색합니다
     *
     * @return 페이징 처리된 게시글 목록 (PostListResponse)
     */
    public Page<PostListResponse> searchPosts(CommunitySearchRequestDto searchRequestDto) {
        Sort sort = Sort.by(searchRequestDto.getDirection(), searchRequestDto.getSort().getFieldName());
        Pageable pageable = PageRequest.of(searchRequestDto.getPage(), searchRequestDto.getSize(), sort);
        List<CommunityPost> postList = communityQueryDslRepository.findAllByKeywordAndCategory(searchRequestDto, pageable);
        List<PostListResponse> list = postList.stream()
                .map(post -> PostListResponse.from(post, 0))
                .toList();
        return new PageImpl<>(list, pageable, list.size());
    }

    /**
     * 특정 게시글의 상세 정보를 조회합니다.
     *
     * @param postId 조회할 게시글의 ID
     * @param user   현재 로그인한 사용자 (좋아요 여부 확인용, null 가능)
     * @return 게시글 상세 정보
     */
    @Transactional
    public PostDetailResponse getPost(Long postId, User user) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));
        post.setViews(post.getViews() + 1);

        // 좋아요 여부 확인
        boolean isLiked = false;
        if (user != null) {
            isLiked = communityLikeRepository.findByUserAndPost(user, post).isPresent();
        }

        List<CommunityComment> comments = communityCommentRepository.findAllByPost(post);
        List<CommentResponse> commentResponses = convertToHierarchy(comments);

        return PostDetailResponse.of(post, commentResponses, isLiked);
    }

    private List<CommentResponse> convertToHierarchy(List<CommunityComment> comments) {
        List<CommentResponse> result = new ArrayList<>();
        Map<Long, CommentResponse> map = new HashMap<>();

        comments.forEach(comment -> {
            CommentResponse dto = CommentResponse.from(comment);
            map.put(dto.getCommentId(), dto);

            if (comment.getParent() != null) {
                // 대댓글인 경우: 부모의 children 리스트에 추가
                CommentResponse parentDto = map.get(comment.getParent().getCommentIdx());
                if (parentDto != null) {
                    parentDto.getChildren().add(dto);
                }
            } else {
                // 최상위 댓글인 경우: 결과 리스트에 바로 추가
                result.add(dto);
            }
        });
        return result;
    }

    /**
     * 새로운 게시글을 생성하고 데이터베이스에 저장합니다.
     *
     * @param requestDto 게시글 생성에 필요한 데이터 DTO
     * @param user       게시글을 작성하는 인증된 사용자
     * @return 생성된 게시글의 ID
     */
    @Transactional
    public Long createPost(PostCreateRequest requestDto, User loginUser) {
        User writer = userRepository.findById(loginUser.getUserIdx())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + loginUser.getUserIdx()));

        CommunityPost newPost = requestDto.toEntity(writer);
        CommunityPost savedPost = communityPostRepository.save(newPost);
        return savedPost.getPostIdx();
    }

    /**
     * 게시글을 수정합니다.
     * <p>
     * 수정을 요청한 사용자가 게시글의 작성자인지 확인하는 권한 검증을 수행합니다.
     *
     * @param requestDto 수정할 게시글의 정보가 담긴 DTO
     * @param user       수정을 요청한 사용자
     * @throws EntityNotFoundException 게시글이 존재하지 않을 경우
     * @throws IllegalAccessException  수정 권한이 없는 경우
     */
    @Transactional
    public void updatePost(PostUpdateRequest requestDto, User loginUser) {
        User user = userRepository.findById(loginUser.getUserIdx())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + loginUser.getUserIdx()));

        CommunityPost post = communityPostRepository.findById(requestDto.getPostIdx())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().equals(user)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        // DTO의 내용으로 엔티티 업데이트
        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());
        post.setCategory(requestDto.getCategory());
        post.setStatus(requestDto.getStatus());
        post.setUpdatedAt(ZonedDateTime.now());
    }

    /**
     * 게시글을 삭제합니다.
     * <p>
     * 삭제를 요청한 사용자가 게시글의 작성자인지 확인하는 권한 검증을 수행합니다.
     *
     * @param postId 삭제할 게시글의 ID
     * @param user   삭제를 요청한 사용자
     * @throws EntityNotFoundException 게시글이 존재하지 않을 경우
     * @throws IllegalAccessException  삭제 권한이 없는 경우
     */
    @Transactional
    public void deletePost(Long postId, User user) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        // 권한 검증
        if (!Objects.equals(post.getUser().getUserIdx(), user.getUserIdx())) {
            throw new IllegalStateException("게시글을 삭제할 권한이 없습니다.");
        }

        // TODO: 연관된 댓글, 좋아요 등도 함께 삭제하는 로직이 필요할 수 있습니다.
        // Cascade 옵션에 따라 동작이 달라집니다.
        communityPostRepository.delete(post);
    }

    /**
     * 새로운 댓글을 생성하고 데이터베이스에 저장합니다.
     *
     * @param requestDto 댓글 생성에 필요한 데이터 DTO
     * @param user       댓글을 작성하는 인증된 사용자
     */
    @Transactional
    public void createComment(CommentCreateRequest requestDto, User loginUser) {
        User writer = userRepository.findById(loginUser.getUserIdx())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + loginUser.getUserIdx()));

        CommunityPost post = communityPostRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 2. 부모 댓글 조회 (대댓글인 경우)
        CommunityComment parentComment = null;
        if (requestDto.getParentId() != null) {
            parentComment = communityCommentRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("부모 댓글을 찾을 수 없습니다: " + requestDto.getParentId()));
        }

        // 3. 댓글 엔티티 생성
        CommunityComment newComment = CommunityComment.builder()
                .post(post)
                .user(writer)
                .parent(parentComment)
                .content(requestDto.getContent())
                .createdAt(ZonedDateTime.now())
                .build();

        // 4. 댓글 저장
        communityCommentRepository.save(newComment);
    }

    /**
     * 게시글에 대한 '좋아요' 상태를 토글(추가/삭제)합니다.
     *
     * @param postId 좋아요를 누를 게시글의 ID
     * @param user   좋아요를 누르는 사용자
     */
    @Transactional
    public void toggleLike(Long postId, User user) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        Optional<CommunityLike> likeOptional = communityLikeRepository.findByUserAndPost(user, post);

        if (likeOptional.isPresent()) {
            // 이미 좋아요를 누른 경우 -> 좋아요 취소
            communityLikeRepository.delete(likeOptional.get());
            post.setLikeCount(post.getLikeCount() - 1);
        } else {
            // 좋아요를 누르지 않은 경우 -> 좋아요 추가
            CommunityLike newLike = CommunityLike.builder()
                    .user(user)
                    .post(post)
                    .likedAt(ZonedDateTime.now())
                    .build();
            communityLikeRepository.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);
        }
    }
}