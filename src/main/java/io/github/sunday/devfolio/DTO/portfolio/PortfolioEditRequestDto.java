package io.github.sunday.devfolio.dto.portfolio;

import lombok.*;

/**
 * 포트폴리오 수정 요청용 DTO
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortfolioEditRequestDto {
    /**
     * 포트폴리오 식별자
     */
    private Long portfolioIdx;

    /**
     * 작성 요청용 DTO
     */
    private PortfolioWriteRequestDto writeDto;

    /**
     * 썸네일 URL
     */
    private String thumbnailUrl;
}
