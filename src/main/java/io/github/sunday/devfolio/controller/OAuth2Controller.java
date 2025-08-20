package io.github.sunday.devfolio.controller;

import io.github.sunday.devfolio.entity.table.user.AuthProvider;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuth2Controller {

    private final UserService userService;

    public OAuth2Controller(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/oauth2/success")
    public String oauth2Success(@AuthenticationPrincipal OAuth2User oauthUser, HttpSession session) {
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String providerId = oauthUser.getAttribute("sub");

        // 이메일로 기존 사용자 조회
        User existingUser = userService.findByEmail(email);

        if (existingUser != null) {
            if (existingUser.getOauthProvider() == null) {
                // 기존 로컬 회원이면 소셜 로그인 차단
                return "redirect:/login?error=email";
            } else {
                // 기존 소셜 회원이면 로그인 처리
                return "redirect:/main";
            }
        }

        // 신규 소셜 회원이면 닉네임 자동 생성
        String nickname = userService.generateValidNickname(name);

        // 신규 소셜 회원이면 아이디 자동 생성
        String generatedId = userService.generateUserId(email);

        User newUser = new User(generatedId, email, nickname, null, providerId, AuthProvider.GOOGLE);
        userService.saveUser(newUser);

        return "redirect:/main";
    }
}