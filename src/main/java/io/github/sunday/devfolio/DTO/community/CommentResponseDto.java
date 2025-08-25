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
@Builder
public class CommentResponseDto {
    private final Long commentId;
    private final String content;
    private final String authorNickname;
    private final String authorProfileImg;
    private final ZonedDateTime createdAt;
    private final List<CommentResponseDto> children = new ArrayList<>();
    private final Long authorUserIdx;


    /**
     * CommunityComment 엔티티를 CommentResponse DTO로 변환합니다.
     * @param comment 원본 CommunityComment 엔티티
     * @return 변환된 CommentResponse 객체
     */
    public static CommentResponseDto from(CommunityComment comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getCommentIdx())
                .content(comment.getContent())
                .authorNickname(comment.getUser().getNickname())
                .authorProfileImg(comment.getUser().getProfileImg())
                .createdAt(comment.getCreatedAt())
                .authorUserIdx(comment.getUser().getUserIdx()) // [추가] 작성자 ID 설정
                .build();
    }
}