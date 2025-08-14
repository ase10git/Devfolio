package io.github.sunday.devfolio.dto.portfolio;

import io.github.sunday.devfolio.annotation.portfolio.PortfolioCategoryValid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 포트폴리오 작성 요청 데이터를 받는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioWriteRequestDto {

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
    @Pattern(regexp = "", message = "잘못된 날짜 형식입니다.")
    private String startDate;

    /**
     * 프로젝트 종료일
     */
    @Pattern(regexp = "", message = "잘못된 날짜 형식입니다.")
    private String endDate;

    /**
     * 포트폴리오 내용
     */
    @NotBlank(message = "내용을 입력해주세요")
    private String description;

    /**
     * 포트폴리오 카테고리
     */
    private List<Integer> categories = new ArrayList<>();

    /**
     * 포트폴리오 썸네일 이미지
     */
    // Todo : 파일 확장자 검증 로직 필요
    private MultipartFile thumbnail;

    /**
     * 포트폴리오 이미지
     */
    // Todo : 파일 확장자 검증 로직 필요
    @Size(max = 50, message = "이미지는 최대 50까지 업로드할 수 있습니다.")
    private List<MultipartFile> images;

    /**
     * 프로젝트 날짜 검증
     */
    @AssertTrue(message = "프로젝트의 시작 날짜와 종료 날짜가 잘못 입력되었습니다.")
    private boolean isDateValid() {
        if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty()) {
            return true;
        }
        LocalDateTime startLocalDate = LocalDateTime.parse(startDate);
        LocalDateTime endLocalDate = LocalDateTime.parse(endDate);
        return endLocalDate.isAfter(startLocalDate);
    }
}
