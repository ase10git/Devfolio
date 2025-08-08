package io.github.sunday.devfolio.entity.table.profile;

import io.github.sunday.devfolio.entity.table.user.User;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 사용자 간 팔로우(Follow) 관계를 나타내는 엔티티입니다.
 */
@Entity
@Table(name = "follows")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Follow {
    /** 팔로우 관계 고유 식별자 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follower_id")
    private Long id;

    /** 팔로우 하는 사용자 참조 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_idx", nullable = false)
    private User follower;

    /** 팔로우 받는 사용자 참조 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_idx", nullable = false)
    private User followed;

    /** 팔로우 일시 */
    @Column(name = "followed_at", nullable = false)
    private LocalDateTime followedAt;

    /** 객체 동등성 비교 */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follow follow = (Follow) o;
        return Objects.equals(id, follow.id);
    }
    /** 해시코드계산 */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}