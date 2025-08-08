package io.github.sunday.devfolio.repository;

import io.github.sunday.devfolio.entity.table.profile.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    /** 특정 사용자가 팔로우 중인 관계 조회 */
    List<Follow> findAllByFollower(User follower);

    /** 특정 사용자를 팔로우 중인 관계 조회 */
    List<Follow> findAllByFollowed(User followed);

    /** 팔로우 여부 확인 */
    boolean existsByFollowerAndFollowed(User follower, User followed);

    /** 팔로우 관계 삭제 (언팔로우) */
    void deleteByFollowerAndFollowed(User follower, User followed);

    /** 팔로잉 수 카운트 */
    long countByFollower(User follower);

    /** 팔로워 수 카운트 */
    long countByFollowed(User followed);
}
