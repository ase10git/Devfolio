package io.github.sunday.devfolio.dto.community;

import jakarta.validation.constraints.NotBlank;
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
    private Long postId;
    private Long parentId;
    @NotBlank(message = "잘못된 댓글 형식입니다.")
    private String content;
}