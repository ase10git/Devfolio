package io.github.sunday.devfolio.repository.community;

import io.github.sunday.devfolio.entity.table.community.CommunityComment;
import io.github.sunday.devfolio.entity.table.community.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 커뮤니티 댓글(CommunityComment) 엔티티에 대한 데이터 액세스 인터페이스.
 */
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    List<CommunityComment> findAllByPost(CommunityPost post);
    long countByPostPostIdx(Long postId);
}