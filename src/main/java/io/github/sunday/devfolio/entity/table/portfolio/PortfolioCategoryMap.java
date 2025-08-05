package io.github.sunday.devfolio.entity.table.portfolio;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * 포트폴리오와 카테고리의 매핑 관계를 나타내는 JPA 엔티티 클래스입니다.
 * <p>
 * 이 클래스는 포트폴리오와 카테고리 간의 N:M 관계를 관리하며, 다음과 같은 주요 기능을 제공합니다:
 * <ul>
 *     <li>포트폴리오와 카테고리 간의 매핑 관계 관리</li>
 *     <li>중복 매핑 방지를 위한 유니크 제약 조건</li>
 *     <li>매핑 관계의 식별자 관리</li>
 * </ul>
 * </p>
 *
 * <p>
 * 연관 관계:
 * <ul>
 *     <li>{@link Portfolios}: 매핑된 포트폴리오와의 관계 (1:N)</li>
 *     <li>{@link PortfolioCategories}: 매핑된 카테고리와의 관계 (1:N)</li>
 * </ul>
 * </p>
 *
 * @since 2025-08-05
 */
@Entity
@Table(
    name = "portfolio_category_map",
    uniqueConstraints = @UniqueConstraint(columnNames = {"portfolio_idx", "category_idx"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioCategoryMap {

    /**
     * 매핑 관계의 고유 식별자 (기본키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "map_idx", nullable = false)
    private Long mapIdx;

    /**
     * 매핑된 포트폴리오
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_idx", nullable = false)
    private Portfolios portfolios;

    /**
     * 매핑된 카테고리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_idx", nullable = false)
    private PortfolioCategories categories;

    /**
     * 객체의 동등성 비교를 위한 equals 메서드
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioCategoryMap that = (PortfolioCategoryMap) o;
        return Objects.equals(mapIdx, that.mapIdx);
    }

    /**
     * 객체의 해시 코드를 반환하는 메서드
     */
    @Override
    public int hashCode() {
        return Objects.hash(mapIdx);
    }
}
