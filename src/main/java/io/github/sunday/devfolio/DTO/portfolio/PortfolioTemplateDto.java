package io.github.sunday.devfolio.dto.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * Alan Ai에게 추천받은 포트폴리오 템플릿 응답용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortfolioTemplateDto {

    /**
     * HTML 태그 타입
     */
    private String tagType;

    /**
     * 목차 내용
     */
    private String value;
}
