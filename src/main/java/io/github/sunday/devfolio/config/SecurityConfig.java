package io.github.sunday.devfolio.config;

import io.github.sunday.devfolio.service.CustomOAuth2UserService;
import io.github.sunday.devfolio.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 관련 설정을 정의하는 클래스입니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public CustomOAuth2UserService customOAuth2UserService(UserService userService) {
        return new CustomOAuth2UserService(userService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/main", "/signup", "/login", "/email/**", "/check/**", "/portfolio/**", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/assets/**", "/ckeditor5/**", "/prompts/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/community", "/community/{postId}").permitAll()
                        .requestMatchers("/community/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("loginId")
                        .defaultSuccessUrl("/main", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/main")
                        .permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/main", true)
                        .failureHandler((request, response, exception) -> {
                            String errorMessage = "로그인에 실패했습니다.";
                            if (exception instanceof OAuth2AuthenticationException authEx) {
                                String desc = authEx.getError().getDescription();
                                errorMessage = (desc != null) ? desc : authEx.getMessage();
                            }
                            // 세션에 저장 (1회성)
                            request.getSession().setAttribute("loginErrorMessage", errorMessage);
                            // 다시 로그인 페이지로 리다이렉트
                            response.sendRedirect("/login?error");
                        })
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

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