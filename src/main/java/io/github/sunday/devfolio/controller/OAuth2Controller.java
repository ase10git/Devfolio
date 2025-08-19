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

        // 이미 해당 이메일로 가입된 사용자가 있으면 로그인 차단
        User existingUser = userService.findByEmail(email);
        if (existingUser != null) {
            return "redirect:/login?error=email";
        }

        // 닉네임 자동 생성
        String nickname = userService.generateValidNickname(name);

        // 아이디 자동 생성
        String generatedId = userService.generateUserId(email);

        User newUser = new User(generatedId, email, nickname, null, providerId, AuthProvider.GOOGLE);
        userService.saveUser(newUser);

        return "redirect:/main";
    }
}