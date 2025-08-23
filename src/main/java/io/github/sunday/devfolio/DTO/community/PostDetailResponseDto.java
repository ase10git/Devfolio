package io.github.sunday.devfolio.dto.community;

import io.github.sunday.devfolio.entity.table.community.Category;
import io.github.sunday.devfolio.entity.table.community.CommunityPost;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 게시글 상세 조회 응답을 위한 DTO.
 */
@Getter
@Builder
public class PostDetailResponseDto {
    private final Long postIdx;
    private final String title;
    private final String content;
    private final String authorNickname;
    private final String authorProfileImg;
    private final Long authorUserIdx;
    private final ZonedDateTime createdAt;
    private final Integer views;
    private final Integer likeCount;
    private final List<CommentResponseDto> comments;
    private final long totalCommentCount;
    private boolean likedByCurrentUser;
    private final Category category;
    private final String status;

    public static PostDetailResponseDto of(CommunityPost post, List<CommentResponseDto> comments, long totalCommentCount, boolean isLiked) {
        return PostDetailResponseDto.builder()
                .postIdx(post.getPostIdx())
                .title(post.getTitle())
                .content(post.getContent())
                .authorNickname(post.getUser().getNickname())
                .authorProfileImg(post.getUser().getProfileImg())
                .authorUserIdx(post.getUser().getUserIdx())
                .createdAt(post.getCreatedAt())
                .views(post.getViews())
                .likeCount(post.getLikeCount())
                .comments(comments)
                .totalCommentCount(totalCommentCount)
                .likedByCurrentUser(isLiked)
                .category(post.getCategory())
                .status(post.getStatus())
                .build();
    }

}