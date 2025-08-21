package io.github.sunday.devfolio.dto.community;

import io.github.sunday.devfolio.annotation.community.CommunityCategoryValid;
import io.github.sunday.devfolio.enums.CommunitySort;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.domain.Sort;

/**
 * 커뮤니티 검색 요청용 DTO
 * 페이지, 페이지 크기, 정렬 기준, 정렬 방향, 검색 키워드, 카테고리
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunitySearchRequestDto {

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
     * 검색 키워드
     */
    @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s]{0,50}$",
            message = "검색어는 50자 이내의 한글, 영문, 숫자만 가능합니다")
    private String keyword;

    /**
     * 카테고리
     */
    @CommunityCategoryValid
    private String categoryName;

    /**
     * 정렬 기준
     */
    private CommunitySort sort = CommunitySort.UPDATED_AT;

    /**
     * 정렬 방향
     */
    private Sort.Direction direction = Sort.Direction.DESC;

    @AssertTrue(message = "유효하지 않은 정렬 기준입니다.")
    public boolean isValidSort() {
        if (sort == null) return true;
        return CommunitySort.fromFieldName(sort.getFieldName()) != null;
    }
}
