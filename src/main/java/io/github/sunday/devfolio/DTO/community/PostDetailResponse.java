package io.github.sunday.devfolio.dto.community;

import io.github.sunday.devfolio.entity.table.community.CommunityPost;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 게시글 상세 조회 응답을 위한 DTO.
 */
@Getter
public class PostDetailResponse {
    private final Long postIdx;
    private final String title;
    private final String content;
    private final String authorNickname;
    private final String authorProfileImg;
    private final ZonedDateTime createdAt;
    private final Integer views;
    private final Integer likeCount;
    private final List<CommentResponse> comments;
    private boolean likedByCurrentUser;

    @Builder
    private PostDetailResponse(Long postIdx, String title, String content, String authorNickname, String authorProfileImg, ZonedDateTime createdAt, Integer views, Integer likeCount, List<CommentResponse> comments, boolean likedByCurrentUser) {
        this.postIdx = postIdx;
        this.title = title;
        this.content = content;
        this.authorNickname = authorNickname;
        this.authorProfileImg = authorProfileImg;
        this.createdAt = createdAt;
        this.views = views;
        this.likeCount = likeCount;
        this.comments = comments;
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public static PostDetailResponse of(CommunityPost post, List<CommentResponse> comments, boolean isLiked) {
        return PostDetailResponse.builder()
                .postIdx(post.getPostIdx())
                .title(post.getTitle())
                .content(post.getContent())
                .authorNickname(post.getUser().getNickname())
                .authorProfileImg(post.getUser().getProfileImg())
                .createdAt(post.getCreatedAt())
                .views(post.getViews())
                .likeCount(post.getLikeCount())
                .comments(comments)
                .likedByCurrentUser(isLiked)
                .build();
    }


}