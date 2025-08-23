package io.github.sunday.devfolio.dto.portfolio;

import io.github.sunday.devfolio.enums.PortfolioSort;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
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
public class PortfolioPageRequestDto {

    /**
     * 페이지
     */
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    private int page = 0;

    /**
     * 페이지 크기
     */
    @Min(value = 1, message = "페이지 최소 크기는 1 이상이어야 합니다.")
    @Max(value = 100, message = "페이지 최대 크기는 100 이하여야 합니다.")
    private int size = 20;

    /**
     * 정렬 기준
     */
    private PortfolioSort sort = PortfolioSort.UPDATED_AT;

    /**
     * 정렬 방향
     */
    private Sort.Direction direction = Sort.Direction.DESC;

    @AssertTrue(message = "유효하지 않은 정렬 기준입니다.")
    public boolean isValidSort() {
        if (sort == null) return true;
        return PortfolioSort.fromName(sort.name()) != null;
    }
}
