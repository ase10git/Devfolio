package io.github.sunday.devfolio.service.auth.impl;

import io.github.sunday.devfolio.service.auth.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationCode(String email, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[Devfolio] 이메일 인증 코드");
        message.setText("인증 코드는 다음과 같습니다: " + verificationCode);
        mailSender.send(message);
    }
}