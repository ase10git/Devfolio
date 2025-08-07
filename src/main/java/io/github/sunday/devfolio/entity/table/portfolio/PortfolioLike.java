package io.github.sunday.devfolio.entity.table.portfolio;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 포트폴리오 좋아요 정보를 나타내는 JPA 엔티티 클래스입니다.
 * <p>
 * 이 클래스는 포트폴리오에 대한 사용자의 좋아요 정보를 관리하며, 다음과 같은 주요 기능을 제공합니다:
 * <ul>
 *     <li>포트폴리오 좋아요 정보 관리</li>
 *     <li>시간 정보 관리 (좋아요 일시)</li>
 *     <li>고유한 좋아요 제약 조건 관리 (한 사용자가 한 포트폴리오에 대해 하나의 좋아요만 가능)</li>
 * </ul>
 * </p>
 *
 * <p>
 * 연관 관계:
 * <ul>
 *     <li>{User}: 좋아요를 누른 사용자와의 관계 (1:N)</li>
 *     <li>{@link Portfolio}: 좋아요된 포트폴리오와의 관계 (1:N)</li>
 * </ul>
 * </p>
 *
 * @since 2025-08-05
 */

@Entity
@Table(
    name = "portfolio_likes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_idx", "portfolio_idx"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioLike {

    /**
     * 좋아요의 고유 식별자 (기본키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_idx")
    private Long likeIdx;

    /**
     * 좋아요를 누른 사용자
     */
    // Todo : 병합 후 사용
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_idx", nullable = false)
//    private User user;

    /**
     * 좋아요를 받은 포트폴리오
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_idx", nullable = false)
    private Portfolio portfolio;

    /**
     * 좋아요가 생성된 시점
     */
    @Column(name = "liked_at", nullable = false)
    private ZonedDateTime likedAt;

    /**
     * 객체의 동등성 비교를 위한 equals 메서드
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioLike like = (PortfolioLike) o;
        return Objects.equals(likeIdx, like.likeIdx);
    }

    /**
     * 객체의 해시 코드를 반환하는 메서드
     */
    @Override
    public int hashCode() {
        return Objects.hash(likeIdx);
    }
}
