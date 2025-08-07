package io.github.sunday.devfolio.entity.table.community;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 커뮤니티 게시글에 달린 댓글을 나타내는 엔티티입니다.
 * <p>자기 자신을 부모로 지정해 다단계 답글 구조를 지원합니다.</p>
 */
@Entity
@Table(name = "community_comments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CommunityComments {

    /** 댓글 고유 식별자 (자동 생성) */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_idx")
    private Long commentIdx;

    /** 댓글 작성자 사용자 정보 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    /** 부모 댓글 (답글인 경우 지정, 최상위 댓글은 null) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_idx")
    private CommunityComments parent;

    /** 이 댓글이 속한 게시글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_idx", nullable = false)
    private CommunityPost post;

    /** 댓글 내용 */
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    /** 댓글 작성 시각 */
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    /** 댓글 수정 시각 */
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommunityComments communityComments = (CommunityComments) o;
        return Objects.equals(commentIdx, communityComments.commentIdx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentIdx);
    }
}
