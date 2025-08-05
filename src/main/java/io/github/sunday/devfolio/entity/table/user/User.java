package io.github.sunday.devfolio.entity.table.user;

import io.github.sunday.devfolio.util.ZonedDateTimeConverter;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx")
    private Long userIdx;

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(name = "oauth_provider", length = 50)
    private String oauthProvider;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(name = "profile_img", length = 512)
    private String profileImg;

    @Column(name = "github_url", length = 255)
    private String githubUrl;

    @Column(name = "blog_url", length = 255)
    private String blogUrl;

    @Column(name = "affiliation", length = 100)
    private String affiliation;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(userIdx, user.userIdx)
                && Objects.equals(loginId, user.loginId)
                && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userIdx, loginId, email);
    }
}