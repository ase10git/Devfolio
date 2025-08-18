package io.github.sunday.devfolio.repository.community;

import io.github.sunday.devfolio.entity.table.community.CommunityLike;
import io.github.sunday.devfolio.entity.table.community.CommunityPost;
import io.github.sunday.devfolio.entity.table.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 커뮤니티 '좋아요'(CommunityLike) 엔티티에 대한 데이터 액세스 인터페이스.
 */
public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
    Optional<CommunityLike> findByUserAndPost(User user, CommunityPost post);
}