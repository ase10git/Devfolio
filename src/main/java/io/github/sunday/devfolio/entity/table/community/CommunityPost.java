package io.github.sunday.devfolio.entity.table.community;

import io.github.sunday.devfolio.entity.table.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 커뮤니티 게시글 엔티티입니다.
 * <p>
 * 제목, 내용, 상태, 조회수, 좋아요 수, 생성/수정 일시, 카테고리 등의
 * 필드와 연관된 이미지, 댓글, 좋아요 목록을 관리합니다.
 * </p>
 */
@Entity
@Table(name = "community_posts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CommunityPost {

    /** PK: 게시글 고유 식별자 (자동 생성) */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_idx")
    private Long postIdx;

    /** 작성자 사용자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    /** 게시글 제목 */
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    /** 게시글 내용 */
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    /** 게시글 상태 (기본값: ACTIVE) */
    @Column(name = "status", length = 20, nullable = false)
    @ColumnDefault("'ACTIVE'")
    private String status;

    /** 조회수 */
    @Column(name = "views", nullable = false)
    @ColumnDefault("0")
    private Integer views;

    /** 좋아요 수 */
    @Column(name = "like_count", nullable = false)
    @ColumnDefault("0")
    private Integer likeCount;

    /** 댓글 수 */
    @Column(name = "comment_count", nullable = false)
    @ColumnDefault("0")
    private Integer commentCount = 0;

    /** 생성 일시 */
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    /** 수정 일시 */
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    /** 게시글 카테고리 (study, question, general) */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, columnDefinition = "VARCHAR(20)")
    private Category category;

    /** 참조 무결성 제약조건 해결 위한 필드 **/
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityImage> images = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }

    /**
     * 검색을 위한 tsvector
     */
    @Column(
            name = "search_vector",
            columnDefinition = "tsvector",
            insertable = false,
            updatable = false
    )
    private String searchVector;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommunityPost communityPost = (CommunityPost) o;
        return Objects.equals(postIdx, communityPost.postIdx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postIdx);
    }
}
