package io.github.sunday.devfolio.repository;

import io.github.sunday.devfolio.entity.table.user.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findTopByEmailOrderByExpiredAtDesc(String email);

    void deleteByExpiredAtBefore(ZonedDateTime now);
}