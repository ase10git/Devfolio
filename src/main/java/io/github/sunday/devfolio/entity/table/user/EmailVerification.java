package io.github.sunday.devfolio.entity.table.user;

import io.github.sunday.devfolio.util.ZonedDateTimeConverter;
import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "email_verifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", updatable = false, nullable = false)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_idx", nullable = false)
    private User user;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "code", nullable = false, length = 6)
    private String code;

    @Convert(converter = ZonedDateTimeConverter.class)
    @Column(name = "expired_at", nullable = false)
    private ZonedDateTime expiredAt;

    @Column(name = "verified", nullable = false)
    private Boolean verified;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailVerification that = (EmailVerification) o;
        return Objects.equals(idx, that.idx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idx);
    }
}