package io.github.sunday.devfolio.entity.table.community;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * 게시글에 첨부된 이미지를 저장하는 엔티티입니다.
 */
@Entity
@Table(name = "community_images")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CommunityImage {

    /** 이미지 고유 식별자 (자동 생성) */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_idx")
    private Long imageIdx;

    /** 이미지가 속한 게시글 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_idx", nullable = false)
    private CommunityPost post;

    /** 이미지 URL */
    @Column(name = "image_url", length = 512, nullable = false)
    private String imageUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommunityImage communityImage = (CommunityImage) o;
        return Objects.equals(imageIdx, communityImage.imageIdx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageIdx);
    }
}
