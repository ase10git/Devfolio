package io.github.sunday.devfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 관련 설정을 정의하는 클래스입니다.
 */
@Configuration
public class SecurityConfig {

    /**
     * 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 등록.
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}