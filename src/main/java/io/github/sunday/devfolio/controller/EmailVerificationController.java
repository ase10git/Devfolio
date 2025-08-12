package io.github.sunday.devfolio.controller;

import io.github.sunday.devfolio.entity.table.user.EmailVerification;
import io.github.sunday.devfolio.repository.EmailVerificationRepository;
import io.github.sunday.devfolio.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.Map;

@RestController
@RequestMapping("/email")
public class EmailVerificationController {

    private final EmailService emailService;
    private final EmailVerificationRepository emailVerificationRepository;

    public EmailVerificationController(EmailService emailService, EmailVerificationRepository emailVerificationRepository) {
        this.emailService = emailService;
        this.emailVerificationRepository = emailVerificationRepository;
    }

    /**
     * 인증 코드 발송 API
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendVerificationCode(@RequestParam String email) {
        String verificationCode = generateCode();
        String emailClean = email.trim().toLowerCase();

        EmailVerification existing = emailVerificationRepository.findTopByEmailOrderByExpiredAtDesc(emailClean).orElse(null);

        if (existing != null) {
            // 기존 데이터가 있으면 코드, 만료 시간, 인증 상태만 변경 후 저장
            existing.setVerificationCode(verificationCode);
            existing.setExpiredAt(ZonedDateTime.now().plusMinutes(5));
            existing.setVerified(false);
            emailVerificationRepository.save(existing);
        } else {
            // 없으면 새로 저장
            EmailVerification emailVerification = EmailVerification.builder()
                    .email(emailClean)
                    .verificationCode(verificationCode)
                    .expiredAt(ZonedDateTime.now().plusMinutes(5))
                    .verified(false)
                    .build();
            emailVerificationRepository.save(emailVerification);
        }

        try {
            emailService.sendVerificationCode(emailClean, verificationCode);
        } catch (Exception e) {
            // 발송 실패 처리(로그, 에러 응답)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 발송 실패");
        }

        return ResponseEntity.ok("인증 코드가 발송되었습니다. 5분 안에 인증을 완료하세요.");
    }

    /**
     * 인증 코드 확인 API
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String verificationCode) {
        String emailClean = email.trim().toLowerCase();
        EmailVerification verification = emailVerificationRepository
                .findTopByEmailOrderByExpiredAtDesc(emailClean)
                .orElse(null);

        if (verification == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("인증 정보가 없습니다.");
        if (verification.getExpiredAt().isBefore(ZonedDateTime.now())) return ResponseEntity.status(HttpStatus.GONE).body("코드가 만료되었습니다.");
        if (!verification.getVerificationCode().equals(verificationCode)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("코드가 일치하지 않습니다.");

        verification.setVerified(true);
        emailVerificationRepository.save(verification);
        return ResponseEntity.ok().body(Map.of("verified", true));
    }

    private static final SecureRandom secureRandom = new SecureRandom();
    private String generateCode() {
        int code = secureRandom.nextInt(1_000_000);
        return String.format("%06d", code);
    }
}