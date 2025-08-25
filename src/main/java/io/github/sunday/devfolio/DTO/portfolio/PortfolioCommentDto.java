package io.github.sunday.devfolio.dto.portfolio;

import io.github.sunday.devfolio.dto.user.WriterDto;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 포트폴리오 댓글 응답 Dto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioCommentDto {
    /**
     * 댓글의 고유 식별자 (기본키)
     */
    private Long commentIdx;

    /**
     * 댓글의 내용
     */
    private String content;

    /**
     * 댓글이 작성된 시점
     */
    private ZonedDateTime createdAt;

    /**
     * 댓글이 수정된 시점
     */
    private ZonedDateTime updatedAt;

    /**
     * 댓글을 작성한 사용자
     */
    private WriterDto writer;

    /**
     * 댓글이 속한 포트폴리오 식별자
     */
    private Long portfolioIdx;

    /**
     * 대댓글
     */
    private List<PortfolioCommentDto> subComments;
}
