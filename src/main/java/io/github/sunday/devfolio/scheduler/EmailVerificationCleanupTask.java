package io.github.sunday.devfolio.scheduler;

import io.github.sunday.devfolio.repository.auth.EmailVerificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class EmailVerificationCleanupTask {

    private final EmailVerificationRepository emailVerificationRepository;

    // 1분마다 실행
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cleanup() {
        emailVerificationRepository.deleteByExpiredAtBefore(ZonedDateTime.now());
    }
}