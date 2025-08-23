package io.github.sunday.devfolio.controller;

import io.github.sunday.devfolio.dto.UserSignupDto;
import io.github.sunday.devfolio.entity.table.user.AuthProvider;
import io.github.sunday.devfolio.entity.table.user.EmailVerification;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.EmailVerificationRepository;
import io.github.sunday.devfolio.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러 클래스입니다.
 * 회원가입, 로그인, 중복 확인 API 등을 제공합니다.
 */
@Controller
public class UserController {

    private final UserService userService;
    private final EmailVerificationRepository emailVerificationRepository;

    /**
     * UserController 생성자.
     *
     * @param userService 사용자 서비스 객체
     */
    public UserController(UserService userService, EmailVerificationRepository emailVerificationRepository) {
        this.userService = userService;
        this.emailVerificationRepository = emailVerificationRepository;
    }

    /**
     * 회원가입 폼을 보여주는 GET 요청 처리 메서드입니다.
     *
     * @param model Thymeleaf 템플릿에 데이터를 전달할 모델 객체
     * @return signup.html 템플릿 경로
     */
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new UserSignupDto());
        return "signup";
    }

    /**
     * 회원가입 데이터를 처리하는 POST 요청 메서드입니다.
     * 입력값 유효성 검사 및 중복 체크 후 회원가입 처리합니다.
     *
     * @param dto   사용자 입력값을 담은 DTO 객체
     * @param model 에러 메시지를 전달할 모델 객체
     * @return 에러가 없으면 로그인 페이지로 리디렉션, 그렇지 않으면 다시 signup.html 반환
     */
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

        // 아이디 유효성 검사
        if (!userService.isValidLoginId(dto.getLoginId())) {
            model.addAttribute("error", "아이디는 영문/숫자를 사용한 6~20자만 가능합니다.");
            return "signup";
        }

        // 닉네임 유효성 검사
        if (!userService.isValidNickname(dto.getNickname())) {
            model.addAttribute("error", "닉네임은 한글/영문/숫자를 사용한 4~12자만 가능합니다.");
            return "signup";
        }

        // 비밀번호 유효성 검사
        if (!userService.isValidPassword(dto.getPassword())) {
            model.addAttribute("error", "비밀번호는 영문+숫자 필수, 특수문자는 !@#$%^&*()만 가능하며 8~20자여야 합니다.");
            return "signup";
        }

        // 이메일 인증 여부 확인
        EmailVerification verification = emailVerificationRepository
                .findTopByEmailOrderByExpiredAtDesc(dto.getEmail())
                .orElse(null);

        if (verification == null) {
            model.addAttribute("error", "이메일 인증을 진행해 주세요.");
            return "signup";
        }

        // 인증 여부 확인
        if (!Boolean.TRUE.equals(verification.getVerified())) {
            model.addAttribute("error", "이메일 인증이 완료되지 않았습니다.");
            return "signup";
        }

        // 인증 시간 만료 여부(예: 인증 후 5분 내에만 회원가입 가능)
        if (verification.getExpiredAt().isBefore(ZonedDateTime.now())) {
            model.addAttribute("error", "이메일 인증이 만료되었습니다. 다시 인증해 주세요.");
            return "signup";
        }

        // 사용자 생성 및 저장
        User user = User.builder()
                .loginId(dto.getLoginId())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .oauthProvider(AuthProvider.LOCAL)
                .build();
        userService.saveUser(user);

        // 회원가입 성공 시 로그인 페이지로 이동
        return "redirect:/login?signupSuccess=true";
    }

    /**
     * 로그인 아이디 중복 여부를 확인하는 GET API입니다.
     *
     * @param loginId 중복 여부를 확인할 로그인 아이디
     * @return 중복이면 true, 아니면 false
     */
    @ResponseBody
    @GetMapping("/check/loginId")
    public boolean checkLoginId(@RequestParam String loginId) {
        return userService.isLoginIdDuplicate(loginId);
    }

    /**
     * 닉네임 중복 여부를 확인하는 GET API입니다.
     *
     * @param nickname 중복 여부를 확인할 닉네임
     * @return 중복이면 true, 아니면 false
     */
    @ResponseBody
    @GetMapping("/check/nickname")
    public boolean checkNickname(@RequestParam String nickname) {
        return userService.isNicknameDuplicate(nickname);
    }

    /**
     * 로그인 폼을 보여주는 GET 요청 처리 메서드입니다.
     *
     * @return login.html 템플릿 경로
     */
    @GetMapping("/login")
    public String loginForm(HttpServletRequest request, Model model) {
        // 로그인한 사용자인지 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            // 이미 로그인된 사용자라면 메인으로 리다이렉트
            return "redirect:/main";
        }

        // 로그인 에러 메시지 처리
        Object errorMessage = request.getSession().getAttribute("loginErrorMessage");
        if (errorMessage != null) {
            model.addAttribute("loginErrorMessage", errorMessage);
            // 1회성 출력 후 세션에서 제거
            request.getSession().removeAttribute("loginErrorMessage");
        }
        return "login";
    }
}