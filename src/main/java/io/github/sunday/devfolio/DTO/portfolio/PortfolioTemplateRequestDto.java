package io.github.sunday.devfolio.dto.portfolio;

import io.github.sunday.devfolio.annotation.portfolio.PortfolioCategoryValid;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 포트폴리오 템플릿 요청 Dto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioTemplateRequestDto {
    /**
     * 포트폴리오 카테고리 타입
     */
    @NotBlank(message = "카테고리를 입력해주세요")
    @PortfolioCategoryValid
    private String type;
}
