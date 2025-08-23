package io.github.sunday.devfolio.dto.community;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentUpdateRequestDto {
    private Long commentId;
    private String content;
}
