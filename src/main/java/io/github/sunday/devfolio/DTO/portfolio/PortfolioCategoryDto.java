package io.github.sunday.devfolio.dto.portfolio;

import lombok.*;

/**
 * 포트폴리오 카테고리 응답용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioCategoryDto {
    /**
     * 카테고리의 고유 식별자 (기본키)
     */
    private Long categoryIdx;

    /**
     * 카테고리의 이름
     */
    private String name;

    /**
     * 카테고리의 이름(한글)
     */
    private String nameKo;

    /**
     * 카테고리 설명
     */
    private String description;
}
