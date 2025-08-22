package io.github.sunday.devfolio.dto.community;

import io.github.sunday.devfolio.entity.table.community.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 게시글 수정을 요청할 때 사용하는 DTO.
 */
@Getter
@Setter
@NoArgsConstructor
public class PostUpdateRequestDto {
    private Long postIdx; // 어떤 글을 수정할지 식별하기 위한 ID
    private String title;
    private String content;
    private Category category;
    private String status;
}