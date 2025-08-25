package io.github.sunday.devfolio.dto.portfolio;

import lombok.*;

/**
 * 사용자가 좋아요 표시한 포트폴리오의 정보를 응답으로 전송하는 DTO
 * 포트폴리오 리스트용 DTO, 좋아요 표시한 날짜
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioLikeListDto {

    /**
     * 포트폴리오 리스트용 DTO
     */
    private PortfolioListDto portfolioListDto;

    /**
     * 좋아요가 생성된 시점
     */
    private String likedAt;
}
