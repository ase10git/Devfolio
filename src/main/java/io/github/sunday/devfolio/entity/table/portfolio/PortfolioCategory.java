package io.github.sunday.devfolio.entity.table.portfolio;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * 포트폴리오 카테고리 정보를 나타내는 JPA 엔티티 클래스입니다.
 * <p>
 * 이 클래스는 포트폴리오를 분류하는 카테고리 정보를 관리하며, 다음과 같은 주요 기능을 제공합니다:
 * <ul>
 *     <li>카테고리 기본 정보 관리 (이름, 설명)</li>
 *     <li>포트폴리오와의 매핑 관계 관리</li>
 *     <li>유니크한 카테고리 식별자 관리</li>
 * </ul>
 * </p>
 *
 * <p>
 */
@Entity
@Table(name = "portfolio_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioCategory {

    /**
     * 카테고리의 고유 식별자 (기본키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_idx")
    private Long categoryIdx;

    /**
     * 카테고리의 이름
     */
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    /**
     * 카테고리의 이름(한글)
     */
    @Column(name = "name_ko", length = 50, nullable = false)
    private String nameKo;

    /**
     * 카테고리의 설명
     */
    @Column(name = "description")
    private String description;

    /**
     * 객체의 동등성 비교를 위한 equals 메서드
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioCategory portfolioCategory = (PortfolioCategory) o;
        return Objects.equals(categoryIdx, portfolioCategory.categoryIdx);
    }

    /**
     * 객체의 해시 코드를 반환하는 메서드
     */
    @Override
    public int hashCode() {
        return Objects.hash(categoryIdx);
    }
}
