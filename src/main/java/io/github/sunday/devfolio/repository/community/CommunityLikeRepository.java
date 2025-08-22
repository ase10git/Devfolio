package io.github.sunday.devfolio.repository.community;

import io.github.sunday.devfolio.entity.table.community.CommunityLike;
import io.github.sunday.devfolio.entity.table.community.CommunityPost;
import io.github.sunday.devfolio.entity.table.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 커뮤니티 '좋아요'(CommunityLike) 엔티티에 대한 데이터 액세스 인터페이스.
 */
public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
    Optional<CommunityLike> findByUserAndPost(User user, CommunityPost post);

    List<CommunityLike> findAllByUser(User user);

    // 특정 사용자가 특정 게시글을 좋아요 했는지 확인
    boolean existsByUserAndPost(User user, CommunityPost post);

    // 특정 사용자가 특정 게시글 좋아요 삭제
    void deleteByUserAndPost(User user, CommunityPost post);
}