package io.github.sunday.devfolio.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Component
public class CookieUtil {

    public String buildAccessTokenCookie(String token) {
        return ResponseCookie.from("AT", token)
                .httpOnly(true).secure(false)  // 로컬에서는 secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofMinutes(30))
                .build().toString();
    }

    public String buildRefreshTokenCookie(String rtRaw) {
        return ResponseCookie.from("RT", rtRaw)
                .httpOnly(true).secure(false)  // 로컬에서는 secure(false)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(14))
                .build().toString();
    }

    public Optional<String> readCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return Optional.empty();
        return Arrays.stream(req.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public String expireCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true).secure(false)  // 로컬에서는 secure(false)
                .path("/")
                .maxAge(0)
                .build().toString();
    }
}