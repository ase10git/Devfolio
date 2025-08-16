package io.github.sunday.devfolio.dto.portfolio;

import io.github.sunday.devfolio.dto.user.WriterDto;
import lombok.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * 포트폴리오의 상세 정보와 댓글을 제공하는 DTO
 * 포트폴리오의 전체 정보, 댓글, 통계, 작성자 정보
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortfolioDetailDto {

    /**
     * 포트폴리오의 고유 식별자 (기본키)
     */
    private Long portfolioIdx;

    /**
     * 포트폴리오 제목
     */
    private String title;

    /**
     * 프로젝트 시작일
     */
    private LocalDate startDate;

    /**
     * 프로젝트 종료일
     */
    private LocalDate endDate;

    /**
     * 포트폴리오 내용
     */
    private String description;

    /**
     * 포트폴리오 내용의 목차
     */
    private String descriptionIndex;

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
     * 포트폴리오 생성일시
     */
    private ZonedDateTime createdAt;

    /**
     * 포트폴리오 수정일시
     */
    private ZonedDateTime updatedAt;

    /**
     * 포트폴리오 이미지
     */
    private List<PortfolioImageDto> images;

    /**
     * 작성자 정보
     */
    private WriterDto writer;

    /**
     * 포트폴리오 카테고리
     */
    private List<PortfolioCategoryDto> categories;

    /**
     * 댓글 목록
     */
    private List<PortfolioCommentDto> comments;
}
