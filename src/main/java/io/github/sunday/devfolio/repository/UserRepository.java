package io.github.sunday.devfolio.repository;

import io.github.sunday.devfolio.entity.table.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * 사용자(User) 엔티티에 대한 데이터베이스 접근을 처리하는 JPA 리포지토리입니다.
 * Spring Data JPA의 기본 CRUD 기능 외에, 로그인 ID 및 닉네임 중복 검사,
 * 사용자 조회 메서드를 추가로 제공합니다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 주어진 로그인 ID가 이미 존재하는지 확인합니다.
     *
     * @param loginId 확인할 로그인 ID
     * @return 존재하면 true, 존재하지 않으면 false
     */
    boolean existsByLoginId(String loginId);

    /**
     * 주어진 닉네임이 이미 존재하는지 확인합니다.
     *
     * @param nickname 확인할 닉네임
     * @return 존재하면 true, 존재하지 않으면 false
     */
    boolean existsByNickname(String nickname);

    boolean existsByEmail(String email);

    /**
     * 로그인 ID를 통해 사용자 정보를 조회합니다.
     *
     * @param loginId 조회할 로그인 ID
     * @return 해당 로그인 ID를 가진 사용자 정보(Optional), 없으면 Optional.empty()
     */
    Optional<User> findByLoginId(String loginId);

    Optional<User> findByEmail(String email);

    /**
     * 사용자 식별자(userIdx)를 통해 사용자 정보를 조회합니다.
     *
     * @param userIdx 사용자 식별자 (Primary Key와 다를 수 있음)
     * @return 해당 식별자를 가진 사용자 정보(Optional), 없으면 Optional.empty()
     */
    Optional<User> findByUserIdx(Long userIdx);

    boolean existsByNicknameAndUserIdxNot(String nickname, Long userIdx);
}