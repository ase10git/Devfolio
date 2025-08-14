package io.github.sunday.devfolio.service;

public interface EmailService {
    void sendVerificationCode(String email, String verificationCode);
}