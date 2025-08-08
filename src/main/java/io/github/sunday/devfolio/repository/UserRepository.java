package io.github.sunday.devfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    /**
     * 로그인 아이디로 사용자 조회
     */
    Optional<User> findByLoginId(String loginId);

    /** 이메일 중복 확인 */
    boolean existsByEmail(String email);

    /** 닉네임 중복 확인 */
    boolean existsByNickname(String nickname);
}
