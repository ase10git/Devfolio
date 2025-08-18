package io.github.sunday.devfolio.controller;

import io.github.sunday.devfolio.config.AppProps;
import io.github.sunday.devfolio.config.CustomUser;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.UserRepository;
import io.github.sunday.devfolio.service.JwtService;
import io.github.sunday.devfolio.service.RefreshTokenService;
import io.github.sunday.devfolio.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.ui.Model;

import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtService jwt;
    private final CookieUtil cookies;
    private final RefreshTokenService rtService;
    private final AppProps props;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public String login(@RequestParam String loginId,
                                   @RequestParam String password,
                                   HttpServletResponse res) {
        // 1. DB에서 사용자 조회
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // 2. 비밀번호 체크 (BCrypt)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        // 3. AccessToken 생성
        String accessToken = jwt.generateAccessToken(user.getUserIdx(), user.getLoginId(), user.getOauthProvider().name());

        // 4. RefreshToken 생성 & DB 저장
        String rawRt = rtService.newRawToken();
        UUID tokenId = rtService.store(user.getUserIdx(), rawRt);

        // 5. 쿠키 세팅 (path 통일)
        res.addHeader(HttpHeaders.SET_COOKIE, cookies.buildAccessTokenCookie(accessToken));
        res.addHeader(HttpHeaders.SET_COOKIE, cookies.buildRefreshTokenCookie(rawRt));

        return "redirect:/main";
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestHeader("X-Token-Id") UUID tokenId,
            HttpServletRequest req,
            HttpServletResponse res) {

        String rtRaw = cookies.readCookie(req, "RT")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Long userIdx = rtService.getUserIdxByTokenId(tokenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        boolean ok = rtService.verifyAndDelete(userIdx, tokenId, rtRaw);
        if (!ok) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        // DB에서 사용자 정보 조회
        User user = userRepository.findByUserIdx(userIdx)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        String newAT = jwt.generateAccessToken(user.getUserIdx(), user.getLoginId(), user.getOauthProvider().name());
        String newRtRaw = rtService.newRawToken();
        UUID newTokenId = rtService.store(userIdx, newRtRaw);

        res.addHeader(HttpHeaders.SET_COOKIE, cookies.buildAccessTokenCookie(newAT));
        res.addHeader(HttpHeaders.SET_COOKIE, cookies.buildRefreshTokenCookie(newRtRaw));

        return ResponseEntity.ok(Map.of("tokenId", newTokenId));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("X-Token-Id") UUID tokenId,
                                    @AuthenticationPrincipal CustomUser principal,
                                    HttpServletResponse res) {
        // RefreshToken DB 삭제
        rtService.verifyAndDelete(principal.getUserIdx(), tokenId, "");

        // 쿠키 만료
        res.addHeader(HttpHeaders.SET_COOKIE, cookies.expireCookie("AT"));
        res.addHeader(HttpHeaders.SET_COOKIE, cookies.expireCookie("RT"));

        return ResponseEntity.ok(Map.of("message", "logout success"));
    }

    @GetMapping("/main")
    public String mainPage() {
        return "main"; // main.html 렌더링
    }
}