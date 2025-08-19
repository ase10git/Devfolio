package io.github.sunday.devfolio.dto.portfolio;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 포트폴리오 댓글 작성 요청 Dto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioCommentRequestDto {

    /**
     * 댓글이 속한 포트폴리오 식별자
     */
    @NotBlank(message = "포트폴리오 번호를 추가해주세요")
    private Long portfolioIdx;

    /**
     * 댓글의 내용
     */
    @NotBlank(message = "댓글 내용을 입력해주세요")
    private String content;
}
