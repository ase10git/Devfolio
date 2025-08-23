package io.github.sunday.devfolio.repository.community;

import io.github.sunday.devfolio.dto.community.PostListResponseDto;
import io.github.sunday.devfolio.entity.table.community.CommunityPost;
import io.github.sunday.devfolio.entity.table.portfolio.Portfolio;
import io.github.sunday.devfolio.entity.table.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 커뮤니티 게시글(CommunityPost) 엔티티에 대한 데이터 액세스 인터페이스.
 * <p>
 * JpaRepository를 통해 기본적인 CRUD 기능을, JpaSpecificationExecutor를 통해
 * 동적 쿼리 기반의 검색 기능을 지원합니다.
 */
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long>, JpaSpecificationExecutor<CommunityPost> {

    List<CommunityPost> findAllByUser(User target);
}