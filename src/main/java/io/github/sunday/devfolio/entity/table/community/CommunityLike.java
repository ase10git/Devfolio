package io.github.sunday.devfolio.entity.table.community;

import io.github.sunday.devfolio.entity.table.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 사용자가 게시글에 '좋아요'를 남긴 기록을 나타내는 엔티티입니다.
 */
@Entity
@Table(name = "community_likes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CommunityLike {

    /** 좋아요 고유 식별자 (자동 생성) */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_idx")
    private Long likeIdx;

    /** 좋아요를 누른 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    /** 좋아요 대상 게시글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_idx", nullable = false)
    private CommunityPost post;

    /** 좋아요 시각 (기본값 now()) */
    @Column(name = "liked_at", nullable = false)
    @ColumnDefault("now()")
    private ZonedDateTime likedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommunityLike communityLike = (CommunityLike) o;
        return Objects.equals(likeIdx, communityLike.likeIdx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(likeIdx);
    }
}