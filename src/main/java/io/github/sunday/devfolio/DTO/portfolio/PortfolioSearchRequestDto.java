package io.github.sunday.devfolio.dto.portfolio;

import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.domain.Sort;

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
     * 페이지 크기
     */
    @Min(value = 1)
    private int size = 20;

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

    /**
     * 정렬 방향
     */
    private Sort.Direction direction;
}
