package io.github.sunday.devfolio.repository;

import io.github.sunday.devfolio.entity.table.user.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserIdxAndTokenId(Long userIdx, UUID tokenId);
    Optional<RefreshToken> findByTokenId(UUID tokenId);
    void deleteByUserIdxAndTokenId(Long userIdx, UUID tokenId);
    void deleteByExpiresAtBefore(ZonedDateTime now);
}