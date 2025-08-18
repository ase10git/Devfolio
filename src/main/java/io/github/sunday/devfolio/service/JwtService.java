package io.github.sunday.devfolio.service;

import io.github.sunday.devfolio.config.AppProps;
import io.github.sunday.devfolio.config.CustomUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final AppProps props;

    public Key getSigningKey() {
        // nested Jwt 클래스의 secret 사용
        String secret = props.getJwt().getSecret();
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Access Token 생성
     */
    public String generateAccessToken(Long userIdx, String loginId, String oauthProvider) {
        return Jwts.builder()
                .setSubject(String.valueOf(userIdx))
                .claim("loginId", loginId)
                .claim("oauthProvider", oauthProvider)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + props.getJwt().getAccessTtlMs()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Access Token 검증
     */
    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 토큰에서 userIdx 추출
     */
    public Long getUserIdxFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 토큰에서 CustomUser 객체 생성
     */
    public CustomUser getUserFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long userIdx = Long.parseLong(claims.getSubject());
        String loginId = claims.get("loginId", String.class);
        String oauthProvider = claims.get("oauthProvider", String.class);
        String password = claims.get("password", String.class);

        // 관리자 기능은 구현하지 않아서 권한 목록(authorities)은 ROLE_USER로 하드코딩
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new CustomUser(userIdx, loginId, oauthProvider, password, authorities);
    }

    /**
     * 토큰에서 Authentication 객체 생성 (Spring Security)
     */
    public Authentication getAuthentication(String token) {
        CustomUser user = getUserFromToken(token);
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
}