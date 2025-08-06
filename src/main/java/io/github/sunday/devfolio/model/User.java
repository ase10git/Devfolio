package io.github.sunday.devfolio.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "id", nullable = false, unique = true, length = 50)
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

    // 내가 작성한 이력서들
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resume> resumes;

    // 팔로우 관계
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> following;

    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers;
}
