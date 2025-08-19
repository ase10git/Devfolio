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

        User user = userService.findByEmail(email);

        // 동일한 이메일을 사용하는 다른 OAuth2 로그인 막기
        if (user != null && (user.getProviderId() == null || !user.getProviderId().equals(providerId))) {
            return "redirect:/login?error=email";
        }

        if (user == null) {
            String nickname = null;

            //닉네임 중복 검사
            boolean isDuplicate = userService.isNicknameDuplicate(name);

            //닉네임 유효성 검사
            boolean isValid = userService.isValidNickname(name);

            if (!isDuplicate && isValid) {
                nickname = name;
            } else {
                nickname = null;
            }

            user = new User(email, nickname, null, providerId, AuthProvider.GOOGLE);
            userService.saveUser(user);
        }

        return "redirect:/main";
    }
}