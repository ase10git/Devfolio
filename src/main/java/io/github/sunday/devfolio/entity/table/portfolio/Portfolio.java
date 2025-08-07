package io.github.sunday.devfolio.entity.table.portfolio;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * 사용자의 포트폴리오 정보를 나타내는 JPA 엔티티 클래스입니다.
 * <p>
 * 이 클래스는 사용자의 포트폴리오에 대한 모든 정보를 관리하며, 다음과 같은 주요 기능을 제공합니다:
 * <ul>
 *     <li>포트폴리오 기본 정보 관리 (제목, 설명 등)</li>
 *     <li>포트폴리오 통계 정보 관리 (조회수, 좋아요 수)</li>
 *     <li>시간 정보 관리 (생성일, 수정일)</li>
 *     <li>다양한 연관 관계 정의</li>
 * </ul>
 * </p>
 *
 * <p>
 * 연관 관계:
 * <ul>
 *     <li>{Users}: 포트폴리오의 작성자 정보 관리 (1:N)</li>
 * </ul>
 * </p>
 *
 * @since 2025-08-05
 */

@Entity
@Table(name = "portfolios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Portfolio {

    /**
     * 포트폴리오의 고유 식별자 (기본키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_idx", nullable = false)
    private Long portfolioIdx;

    /**
     * 포트폴리오 제목
     */
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    /**
     * 프로젝트 시작일
     */
    @Column(name = "start_date")
    private Date startDate;

    /**
     * 프로젝트 종료일
     */
    @Column(name = "end_date")
    private Date endDate;

    /**
     * 포트폴리오 내용
     */
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * 포트폴리오 조회수
     */
    @Column(name = "views", nullable = false)
    @ColumnDefault("0")
    private Integer views;

    /**
     * 포트폴리오 좋아요 수
     */
    @Column(name = "like_count", nullable = false)
    @ColumnDefault("0")
    private Integer likeCount;

    /**
     * 포트폴리오 생성일시
     */
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    /**
     * 포트폴리오 수정일시
     */
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    // Todo : 병합 후 사용
//    @ManyToOne
//    @JoinColumn(name = "user_idx", nullable = false)
//    private User user;

    /**
     * 객체의 동등성 비교를 위한 equals 메서드
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Portfolio portfolio = (Portfolio) o;
        return Objects.equals(portfolioIdx, portfolio.portfolioIdx);
    }

    /**
     * 객체의 해시 코드를 반환하는 메서드
     */
    @Override
    public int hashCode() {
        return Objects.hash(portfolioIdx);
    }
}
