package io.github.sunday.devfolio.service;

import io.github.sunday.devfolio.config.AppProps;
import io.github.sunday.devfolio.entity.table.user.RefreshToken;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.RefreshTokenRepository;
import io.github.sunday.devfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final UserRepository userRepository;
    private final AppProps props; // secret, ttl, pepper

    /**
     * Refresh Token 원문(rtRaw)을 HMAC-SHA256으로 해싱하여 저장용 토큰 해시 생성
     */
    private String hash(String rtRaw) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            // AppProps 구조에 맞게 수정
            String refreshPepper = props.getJwt().getRefreshPepper();
            mac.init(new SecretKeySpec(refreshPepper.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(rtRaw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 새로운 랜덤 Refresh Token 원문 생성
     */
    public String newRawToken() {
        byte[] buf = new byte[32];
        new SecureRandom().nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    @Transactional
    public UUID store(Long userIdx, String rtRaw) {
        UUID tokenId = UUID.randomUUID();

        // userIdx로 User 엔티티 조회
        User user = userRepository.getReferenceById(userIdx);

        RefreshToken entity = RefreshToken.builder()
                .user(user)
                .tokenId(tokenId)
                .tokenHash(hash(rtRaw))
                .expiresAt(ZonedDateTime.now().plus(props.getJwt().getRefreshTtlMs(), ChronoUnit.MILLIS))
                .createdAt(ZonedDateTime.now())
                .build();
        repo.save(entity);
        return tokenId;
    }

    /**
     * Refresh Token 검증 후 삭제 (회전)
     */
    @Transactional
    public boolean verifyAndDelete(Long userIdx, UUID tokenId, String rtRaw) {
        Optional<RefreshToken> opt = repo.findByUser_UserIdxAndTokenId(userIdx, tokenId);
        if (opt.isEmpty()) return false;

        RefreshToken token = opt.get();

        // 만료 여부 체크
        if (token.getExpiresAt().isBefore(ZonedDateTime.now())) {
            repo.delete(token);
            return false;
        }

        // HMAC 해시 비교 (고정 시간 비교)
        boolean ok = MessageDigest.isEqual(
                token.getTokenHash().getBytes(StandardCharsets.UTF_8),
                hash(rtRaw).getBytes(StandardCharsets.UTF_8)
        );

        // 성공/실패와 관계없이 삭제 (회전)
        repo.delete(token);

        return ok;
    }

    /**
     * tokenId로 userIdx 조회
     */
    @Transactional(readOnly = true)
    public Optional<Long> getUserIdxByTokenId(UUID tokenId) {
        return repo.findUserIdxByTokenId(tokenId);
    }
}