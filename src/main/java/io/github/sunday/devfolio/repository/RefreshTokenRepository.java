package io.github.sunday.devfolio.repository;

import io.github.sunday.devfolio.entity.table.user.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser_UserIdxAndTokenId(Long userIdx, UUID tokenId);
    Optional<RefreshToken> findByTokenId(UUID tokenId);
    void deleteByUser_UserIdxAndTokenId(Long userIdx, UUID tokenId);
    void deleteByExpiresAtBefore(ZonedDateTime now);

    @Query("SELECT rt.user.userIdx FROM RefreshToken rt WHERE rt.tokenId = :tokenId")
    Optional<Long> findUserIdxByTokenId(@Param("tokenId") UUID tokenId);
}