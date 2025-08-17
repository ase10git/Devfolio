package io.github.sunday.devfolio.dto.portfolio;

import lombok.*;

import java.time.ZonedDateTime;

/**
 * 포트폴리오 이미지 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioImageDto {
    /**
     * 이미지의 고유 식별자 (기본키)
     */
    private Long imageIdx;

    /**
     * 이미지가 속한 포트폴리오의 식별자
     */
    private Long portfolioIdx;

    /**
     * 이미지의 URL(AWS S3)
     */
    private String imageUrl;

    /**
     * 이미지의 썸네일 여부
     */
    private Boolean isThumbnail;
}
