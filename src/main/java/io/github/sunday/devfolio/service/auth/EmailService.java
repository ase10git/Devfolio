package io.github.sunday.devfolio.service.auth;

public interface EmailService {
    void sendVerificationCode(String email, String verificationCode);
}