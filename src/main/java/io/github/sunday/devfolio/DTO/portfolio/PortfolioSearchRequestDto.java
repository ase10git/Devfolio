package io.github.sunday.devfolio.dto.portfolio;

import jakarta.validation.constraints.Min;
import lombok.*;

/**
 * 포트폴리오 검색 요청용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioSearchRequestDto {

    /**
     * 페이지
     */
    @Min(value = 0)
    private int page = 0;

    /**
     * 검색 키워드
     */
    private String keyword;

    /**
     * 카테고리
     */
    private String category;

    /**
     * 정렬 기준
     */
    private String sort;
}
