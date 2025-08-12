package io.github.sunday.devfolio.dto.portfolio;

import io.github.sunday.devfolio.dto.user.WriterDto;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 포트폴리오의 정보를 응답으로 전송하는 DTO
 * 포트폴리오의 기본 정보, 카테고리, 작성자, 통계 정보
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioListDto {

    /**
     * 포트폴리오의 고유 식별자 (기본키)
     */
    private Long portfolioIdx;

    /**
     * 포트폴리오 제목
     */
    private String title;

    /**
     * 포트폴리오 내용
     */
    private String description;

    /**
     * 포트폴리오 조회수
     */
    private Integer views;

    /**
     * 포트폴리오 좋아요 수
     */
    private Integer likeCount;

    /**
     * 댓글 수
     */
    private Integer commentCount;

    /**
     * 포트폴리오 수정일시
     */
    private ZonedDateTime updatedAt;

    /**
     * 포트폴리오 썸네일 이미지
     */
    private String imageUrl;

    /**
     * 작성자 정보
     */
    private WriterDto writer;
}
