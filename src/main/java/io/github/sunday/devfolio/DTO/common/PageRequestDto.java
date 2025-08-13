package io.github.sunday.devfolio.dto.common;

import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.domain.Sort;

/**
 * 리스트형 데이터 요청 DTO
 * 페이지, 페이지 크기, 정렬 기준, 정렬 방향
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageRequestDto {

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
     * 정렬 기준
     */
    private String sort;

    /**
     * 정렬 방향
     */
    private Sort.Direction direction;
}
