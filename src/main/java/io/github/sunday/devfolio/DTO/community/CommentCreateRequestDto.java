package io.github.sunday.devfolio.dto.community;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 댓글 생성을 요청할 때 사용하는 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentCreateRequestDto {
    private Long postId;       // 어느 게시글에 달리는 댓글인지 식별
    private Long parentId;     // 부모 댓글 ID (대댓글이 아닐 경우 null)
    private String content;    // 댓글 내용
}