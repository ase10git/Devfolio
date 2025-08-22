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

    public Page<PostListResponseDto> getPosts(Pageable pageable) {
        return communityPostRepository.findPostsWithCommentCount(pageable);
    }

    /**
     * 게시글을 검색합니다
     *
     * @return 페이징 처리된 게시글 목록 (PostListResponse)
     */
    public Page<PostListResponseDto> searchPosts(CommunitySearchRequestDto searchRequestDto) {
        Sort sort = Sort.by(searchRequestDto.getDirection(), searchRequestDto.getSort().getFieldName());
        Pageable pageable = PageRequest.of(searchRequestDto.getPage(), searchRequestDto.getSize(), sort);
        List<CommunityPost> postList = communityQueryDslRepository.findAllByKeywordAndCategory(searchRequestDto, pageable);
        List<PostListResponseDto> list = postList.stream()
                .map(post -> PostListResponseDto.from(post, 0))
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
    public PostDetailResponseDto getPost(Long postId, Long userId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));
        post.setViews(post.getViews() + 1);

        boolean isLiked = false;
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));
            isLiked = communityLikeRepository.findByUserAndPost(user, post).isPresent();
        }

        List<CommunityComment> comments = communityCommentRepository.findAllByPost(post);
        List<CommentResponseDto> commentResponsDtos = convertToHierarchy(comments);

        return PostDetailResponseDto.of(post, commentResponsDtos, isLiked);
    }

    /**
     * 새로운 게시글을 생성하고 데이터베이스에 저장합니다.
     *
     * @param requestDto 게시글 생성에 필요한 데이터 DTO
     * @param user       게시글을 작성하는 인증된 사용자
     * @return 생성된 게시글의 ID
     */
    @Transactional
    public Long createPost(PostCreateRequestDto requestDto, Long userId) {
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));

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
    public void updatePost(PostUpdateRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        CommunityPost post = communityPostRepository.findById(requestDto.getPostIdx())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if (!Objects.equals(post.getUser().getUserIdx(), user.getUserIdx())) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

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
    public void deletePost(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if (!Objects.equals(post.getUser().getUserIdx(), user.getUserIdx())) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        communityPostRepository.delete(post);
    }

    /**
     * 새로운 댓글을 생성하고 데이터베이스에 저장합니다.
     *
     * @param requestDto 댓글 생성에 필요한 데이터 DTO
     * @param user       댓글을 작성하는 인증된 사용자
     */
    @Transactional
    public void createComment(CommentCreateRequestDto requestDto, Long userId) {
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        CommunityPost post = communityPostRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        CommunityComment parentComment = null;
        if (requestDto.getParentId() != null) {
            parentComment = communityCommentRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("부모 댓글을 찾을 수 없습니다."));
        }

        CommunityComment newComment = CommunityComment.builder()
                .user(writer)
                .post(post)
                .parent(parentComment)
                .content(requestDto.getContent())
                .createdAt(ZonedDateTime.now())
                .build();
        communityCommentRepository.save(newComment);
    }

    /**
     * 게시글에 대한 '좋아요' 상태를 토글(추가/삭제)합니다.
     *
     * @param postId 좋아요를 누를 게시글의 ID
     * @param user   좋아요를 누르는 사용자
     */
    @Transactional
    public void toggleLike(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        Optional<CommunityLike> likeOptional = communityLikeRepository.findByUserAndPost(user, post);

        if (likeOptional.isPresent()) {
            communityLikeRepository.delete(likeOptional.get());
            post.setLikeCount(post.getLikeCount() - 1);
        } else {
            CommunityLike newLike = CommunityLike.builder()
                    .user(user)
                    .post(post)
                    .likedAt(ZonedDateTime.now())
                    .build();
            communityLikeRepository.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);
        }
    }

    /**
     * 댓글 엔티티 목록을 부모-자식 관계를 갖는 계층형 DTO 목록으로 변환합니다.
     *
     * @param comments 변환할 댓글 엔티티 목록
     * @return 최상위 댓글만 포함된 계층형 DTO 목록
     */

    private List<CommentResponseDto> convertToHierarchy(List<CommunityComment> comments) {
        List<CommentResponseDto> result = new ArrayList<>();
        Map<Long, CommentResponseDto> map = new HashMap<>();

        // 1. 모든 댓글을 DTO로 변환하고 Map에 ID를 키로 저장
        comments.forEach(comment -> {
            CommentResponseDto dto = CommentResponseDto.from(comment);
            map.put(dto.getCommentId(), dto);
        });

        // 2. 다시 모든 댓글을 순회하며 부모-자식 관계 설정
        comments.forEach(comment -> {
            CommentResponseDto dto = map.get(comment.getCommentIdx());
            if (comment.getParent() != null) {
                // 부모 댓글이 있는 경우(대댓글), 부모의 children 리스트에 추가
                Long parentId = comment.getParent().getCommentIdx();
                CommentResponseDto parentDto = map.get(parentId);
                if (parentDto != null) {
                    parentDto.getChildren().add(dto);
                }
            } else {
                // 부모 댓글이 없는 경우(최상위 댓글), 결과 리스트에 바로 추가
                result.add(dto);
            }
        });

        return result;
    }
}