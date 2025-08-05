package io.github.sunday.devfolio.entity.table.user;

import io.github.sunday.devfolio.util.ZonedDateTimeConverter;
import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 이메일 인증 정보를 나타내는 엔티티 클래스입니다.
 * 사용자가 이메일 인증을 요청할 때 생성되며, 인증 코드와 만료 시간, 인증 여부 등을 저장합니다.
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
     * 이메일 인증의 고유 식별자 (PK).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", updatable = false, nullable = false)
    private Long idx;

    /**
     * 이 인증 요청을 생성한 사용자와의 다대일 관계.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    /**
     * 인증 대상 이메일 주소.
     */
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    /**
     * 인증 코드 (6자리 문자열).
     */
    @Column(name = "code", nullable = false, length = 6)
    private String code;

    /**
     * 인증 코드 만료 시간.
     */
    @Convert(converter = ZonedDateTimeConverter.class)
    @Column(name = "expired_at", nullable = false)
    private ZonedDateTime expiredAt;

    /**
     * 인증 완료 여부.
     * true: 인증 완료, false: 미완료
     */
    @Column(name = "verified", nullable = false)
    private Boolean verified;

    /**
     * 두 EmailVerification 객체가 같은지 비교합니다. 주로 식별자(idx)를 기준으로 비교합니다.
     *
     * @param o 비교할 객체
     * @return 같은 객체이면 true, 아니면 false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailVerification that = (EmailVerification) o;
        return Objects.equals(idx, that.idx);
    }

    /**
     * 해시 코드 생성 (idx 기준).
     *
     * @return 해시 코드 값
     */
    @Override
    public int hashCode() {
        return Objects.hash(idx);
    }
}