package io.github.sunday.devfolio.entity.table.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 이메일 인증 정보를 저장하는 엔티티 클래스입니다.
 *
 * <p>
 * 이 클래스는 이메일 인증 코드, 만료 시간, 인증 여부 등의 정보를 포함하며,
 * 사용자의 이메일 주소를 검증하는 데 사용됩니다.
 * </p>
 */
@Entity
@Table(name = "email_verifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerification {

    /**
     * 이메일 인증 고유 식별자 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_idx", updatable = false, nullable = false)
    private Long verificationIdx;

    /**
     * 인증 대상 이메일 주소
     */
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    /**
     * 인증 코드 (6자리 숫자)
     */
    @Column(name = "verification_code", nullable = false, length = 6)
    private String verificationCode;

    /**
     * 인증 코드의 만료 시간 (기본: 생성 후 5분)
     */
    @Column(name = "expired_at", nullable = false)
    private ZonedDateTime expiredAt;

    /**
     * 인증 완료 여부 (true: 인증 완료, false: 미인증)
     */
    @Column(name = "verified", nullable = false)
    private Boolean verified;

    /**
     * 엔티티가 저장되기 전, 만료 시간이 비어 있다면 5분 뒤로 자동 설정합니다.
     */
    @PrePersist
    protected void onCreate() {
        if (this.expiredAt == null) {
            this.expiredAt = ZonedDateTime.now().plusMinutes(5); // 현재 시각 + 5분
        }
    }

    /**
     * 두 EmailVerification 객체의 동등성을 판단합니다.
     *
     * @param o 비교 대상 객체
     * @return 고유 식별자(idx)가 동일하면 true
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailVerification that = (EmailVerification) o;
        return Objects.equals(verificationIdx, that.verificationIdx);
    }

    /**
     * EmailVerification 객체의 해시코드를 반환합니다.
     *
     * @return 해시값
     */
    @Override
    public int hashCode() {
        return Objects.hash(verificationIdx);
    }
}