package io.github.sunday.devfolio.entity.table.portfolio;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 포트폴리오 댓글 정보를 나타내는 JPA 엔티티 클래스입니다.
 * <p>
 * 이 클래스는 포트폴리오에 달린 댓글과 대댓글을 관리하며, 다음과 같은 주요 기능을 제공합니다:
 * <ul>
 *     <li>댓글 기본 정보 관리 (내용, 작성일, 수정일)</li>
 *     <li>포트폴리오와의 관계 정의</li>
 *     <li>대댓글 구조 지원</li>
 * </ul>
 * </p>
 *
 * <p>
 * 연관 관계:
 * <ul>
 *     <li>{@link Portfolios}: 댓글이 속한 포트폴리오와의 관계 (1:N)</li>
 *     <li>{@link PortfolioComments}: 대댓글 구조를 위한 자기 참조 관계 (1:N)</li>
 * </ul>
 * </p>
 *
 * @since 2025-08-05
 */
@Entity
@Table(name = "portfolio_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioComments {

    /**
     * 댓글의 고유 식별자 (기본키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_idx", nullable = false)
    private Long commentIdx;

    /**
     * 댓글의 내용
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 댓글이 작성된 시점
     */
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    /**
     * 댓글이 수정된 시점
     */
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    /**
     * 댓글을 작성한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private Users user;

    /**
     * 댓글이 속한 포트폴리오
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_idx", nullable = false)
    private Portfolios portfolios;

    /**
     * 부모 댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_idx")
    private PortfolioComments parentComment;

    /**
     * 대댓글 목록
     */
//    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<PortfolioComments> replyComments = new ArrayList<>();

    /**
     * 객체의 동등성 비교를 위한 equals 메서드
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioComments portfolioComments = (PortfolioComments) o;
        return Objects.equals(commentIdx, portfolioComments.commentIdx);
    }

    /**
     * 객체의 해시 코드를 반환하는 메서드
     */
    @Override
    public int hashCode() {
        return Objects.hash(commentIdx);
    }
}
