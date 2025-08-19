package io.github.sunday.devfolio.entity.table.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 사용자 정보를 나타내는 엔티티 클래스입니다.
 * <p>
 * 이 클래스는 'users' 테이블과 매핑되며, 사용자 로그인 정보, OAuth 정보,
 * 프로필 및 메타데이터를 포함합니다.
 * </p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * 사용자 고유 식별자 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_idx")
    private Long userIdx;

    /**
     * 로그인 ID (고유, 필수)
     */
    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    /**
     * OAuth 제공자 이름 (예: GOOGLE, LOCAL)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", length = 50)
    private AuthProvider oauthProvider = AuthProvider.LOCAL;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    /**
     * 이메일 주소 (고유, 필수)
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * 비밀번호 (암호화된 문자열)
     */
    @Column(length = 255)
    private String password;

    /**
     * 사용자 닉네임 (고유, 필수)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    /**
     * 프로필 이미지 URL
     */
    @Column(name = "profile_img", length = 512)
    private String profileImg;

    /**
     * GitHub URL
     */
    @Column(name = "github_url", length = 255)
    private String githubUrl;

    /**
     * 개인 블로그 URL
     */
    @Column(name = "blog_url", length = 255)
    private String blogUrl;

    /**
     * 소속 회사 또는 출신 학교
     */
    @Column(name = "affiliation", length = 100)
    private String affiliation;

    /**
     * 계정 생성 시간 (자동 설정됨)
     */
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    /**
     * 마지막 수정 시간 (자동 설정됨)
     */
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    /**
     * 엔티티가 저장되기 전 자동으로 생성일자를 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }

    /**
     * 엔티티가 업데이트되기 전 자동으로 수정일자를 설정합니다.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }

    /**
     * 두 User 객체의 동등성을 판단합니다.
     *
     * @param o 비교 대상 객체
     * @return 동일한 userIdx, loginId, email을 가지면 true
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(userIdx, user.userIdx)
                && Objects.equals(loginId, user.loginId)
                && Objects.equals(email, user.email);
    }

    /**
     * User 객체의 해시코드를 반환합니다.
     *
     * @return 해시값
     */
    @Override
    public int hashCode() {
        return Objects.hash(userIdx, loginId, email);
    }

    public User(String email, String nickname, String password, String providerId, AuthProvider oauthProvider) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.providerId = providerId;
        this.oauthProvider = oauthProvider;
    }
}