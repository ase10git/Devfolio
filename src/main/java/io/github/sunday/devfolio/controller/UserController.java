package io.github.sunday.devfolio.controller;

import io.github.sunday.devfolio.dto.UserSignupDto;
import io.github.sunday.devfolio.entity.table.user.AuthProvider;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new UserSignupDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute("user") UserSignupDto dto, Model model) {
        // 비밀번호 대조
        if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "signup";
        }
        // 아이디 중복 검사
        if (userService.isLoginIdDuplicate(dto.getLoginId())) {
            model.addAttribute("error", "아이디가 중복인지 확인해 주세요.");
            return "signup";
        }
        // 닉네임 중복 검사
        if (dto.getNickname() != null && !dto.getNickname().isBlank() &&
                userService.isNicknameDuplicate(dto.getNickname())) {
            model.addAttribute("error", "닉네임이 중복인지 확인해 주세요.");
            return "signup";
        }

        // 유효성 검사
        if (!userService.isValidLoginId(dto.getLoginId())) {
            model.addAttribute("error", "아이디는 영문/숫자를 사용한 6~20자만 가능합니다.");
            return "signup";
        }

        if (!dto.getNickname().isBlank() && !userService.isValidNickname(dto.getNickname())) {
            model.addAttribute("error", "닉네임은 한글/영문/숫자를 사용한 4~12자만 가능합니다.");
            return "signup";
        }

        if (!userService.isValidPassword(dto.getPassword())) {
            model.addAttribute("error", "비밀번호는 영문+숫자 필수, 특수문자는 !@#$%^&*()만 가능하며 8~20자여야 합니다.");
            return "signup";
        }

        User user = User.builder()
                .loginId(dto.getLoginId())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .oauthProvider(AuthProvider.LOCAL)
                .build();
        userService.saveUser(user);

        return "redirect:/login?signupSuccess=true";
    }

    // 중복 확인 API
    @ResponseBody
    @GetMapping("/check/loginId")
    public boolean checkLoginId(@RequestParam String loginId) {
        return userService.isLoginIdDuplicate(loginId);
    }

    @ResponseBody
    @GetMapping("/check/nickname")
    public boolean checkNickname(@RequestParam String nickname) {
        return userService.isNicknameDuplicate(nickname);
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

}
