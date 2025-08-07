package io.github.sunday.devfolio.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 사용자 정보를 나타내는 엔티티
 */

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx")
    private Long userIdx;

    @Column(name = "userid", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(name = "oauth_provider", length = 50)
    private String oauthProvider;

    @Column(name = "email", length = 255, unique = true)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "nickname", length = 50, unique = true)
    private String nickname;

    @Column(name = "profile_img", length = 512)
    private String profileImg;

    @Column(name = "github_url", length = 255)
    private String githubUrl;

    @Column(name = "blog_url", length = 255)
    private String blogUrl;

    @Column(name = "affiliation", length = 255)
    private String affiliation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "verified")
    private Boolean verified;

    /**
     * 객체 동등성 비교: 사용자 ID 기준
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userIdx, user.userIdx);
    }

    /**
     * 해시코드 계산: 사용자 ID 기준
     */
    @Override
    public int hashCode() {
        return Objects.hash(userIdx);
    }
}
