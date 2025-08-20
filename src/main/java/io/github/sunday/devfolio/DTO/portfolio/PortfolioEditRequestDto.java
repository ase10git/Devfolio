package io.github.sunday.devfolio.dto.portfolio;

import io.github.sunday.devfolio.annotation.common.DateValid;
import io.github.sunday.devfolio.annotation.portfolio.PortfolioCategoryValid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
     * 포트폴리오 제목
     */
    @NotBlank(message = "제목을 입력해주세요")
    @Size(min = 2, max = 200, message = "제목은 2-200자여야 합니다.")
    @Pattern(regexp = "^[^<>\"'&]+$", message = "특수문자([, ], ^, &, \", ')는 사용할 수 없습니다.")
    private String title;

    /**
     * 프로젝트 시작일
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    /**
     * 프로젝트 종료일
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    /**
     * 포트폴리오 내용
     */
    @NotBlank(message = "내용을 입력해주세요")
    private String description;

    /**
     * 포트폴리오 카테고리
     */
    @PortfolioCategoryValid
    private List<Long> categories = new ArrayList<>();

    /**
     * 포트폴리오 썸네일 이미지
     */
    private MultipartFile thumbnail;

    /**
     * 썸네일 URL
     */
    private String thumbnailUrl;

    /**
     * 썸네일 제거 플래그
     */
    private boolean removeFlag = false;

    /**
     * 포트폴리오 이미지
     */
    @Size(max = 50, message = "이미지는 최대 50까지 업로드할 수 있습니다.")
    private List<String> images;

    /**
     * 프로젝트 날짜 검증
     */
    @AssertTrue(message = "프로젝트의 시작 날짜와 종료 날짜가 잘못 입력되었습니다.")
    private boolean isDateValid() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return endDate.isAfter(startDate);
    }
}
