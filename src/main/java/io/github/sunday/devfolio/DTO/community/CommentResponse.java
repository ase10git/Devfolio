package io.github.sunday.devfolio.dto.community;

import io.github.sunday.devfolio.entity.table.community.CommunityComment;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 댓글 정보 응답을 위한 DTO. 대댓글을 포함하는 계층 구조를 지원합니다.
 */
@Getter
public class CommentResponse {
    private final Long commentId;
    private final String content;
    private final String authorNickname;
    private final String authorProfileImg;
    private final ZonedDateTime createdAt;
    private final List<CommentResponse> children = new ArrayList<>(); // 대댓글 목록

    @Builder
    private CommentResponse(Long commentId, String content, String authorNickname, String authorProfileImg, ZonedDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.authorNickname = authorNickname;
        this.authorProfileImg = authorProfileImg;
        this.createdAt = createdAt;
    }

    public static CommentResponse from(CommunityComment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentIdx())
                .content(comment.getContent())
                .authorNickname(comment.getUser().getNickname())
                .authorProfileImg(comment.getUser().getProfileImg())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}