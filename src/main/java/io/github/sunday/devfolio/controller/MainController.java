package io.github.sunday.devfolio.controller;

import org.springframework.ui.Model;
import io.github.sunday.devfolio.config.CustomUser;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.UserRepository;
import io.github.sunday.devfolio.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserRepository userRepository;

    @GetMapping("/")
    public String mainPage(Model model, @AuthenticationPrincipal CustomUser customUser) {
        if (customUser != null) {
            // userIdx로 DB 조회
            User user = userRepository.findById(customUser.getUserIdx())
                    .orElse(null);

            if (user != null) {
                model.addAttribute("loginId", user.getLoginId());
                model.addAttribute("email", user.getEmail());
                model.addAttribute("nickname", user.getNickname());
            }
        }

        return "main"; // main.html
    }
}