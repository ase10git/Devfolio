package io.github.sunday.devfolio.entity.table.portfolio;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 포트폴리오 이미지를 나타내는 JPA 엔티티 클래스입니다.
 * <p>
 * 이 클래스는 포트폴리오에 포함된 이미지 정보를 관리하며, 다음과 같은 주요 기능을 제공합니다:
 * <ul>
 *     <li>포트폴리오 이미지 정보 관리</li>
 *     <li>이미지 URL 저장(AWS S3)</li>
 *     <li>포트폴리오와의 연관 관계 관리</li>
 * </ul>
 * </p>
 */

@Entity
@Table(name = "portfolio_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioImage {

    /**
     * 이미지의 고유 식별자 (기본키)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_idx", nullable = false)
    private Long imageIdx;

    /**
     * 이미지가 속한 포트폴리오
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_idx", nullable = false)
    private Portfolio portfolio;

    /**
     * 이미지의 URL(AWS S3)
     */
    @Column(
            length = 512,
            name = "image_url",
            nullable = false
    )
    private String imageUrl;

    /**
     * 이미지의 썸네일 여부
     */
    @Column(name = "is_thumbnail")
    @ColumnDefault("false")
    private Boolean isThumbnail;

    /**
     * 생성일
     */
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    /**
     * 만료일
     */
    @Column(name = "expire_at")
    private ZonedDateTime expireAt;

    /**
     * 객체의 동등성 비교를 위한 equals 메서드
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioImage portfolioImage = (PortfolioImage) o;
        return Objects.equals(imageIdx, portfolioImage.imageIdx);
    }

    /**
     * 객체의 해시 코드를 반환하는 메서드
     */
    @Override
    public int hashCode() {
        return Objects.hash(imageIdx);
    }
}
