package io.github.sunday.devfolio.dto.community;

import io.github.sunday.devfolio.entity.table.community.Category;
import io.github.sunday.devfolio.entity.table.community.CommunityPost;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;

/**
 * 게시글 목록 조회 시 응답으로 사용할 데이터 전송 객체(DTO).
 */
@AllArgsConstructor
@Getter
public class PostListResponseDto {
    private final Long postIdx;
    private final String title;
    private final String authorNickname;
    private final Category category;
    private final ZonedDateTime createdAt;
    private final Integer views;
    private final Integer likeCount;
    private final String status;
    private final String content;
    private final String authorProfileImg;
    private final long commentCount;

    /**
     * CommunityPost 엔티티를 PostListResponse DTO로 변환하는 정적 팩토리 메서드.
     * @param post 변환할 CommunityPost 엔티티
     * @return 변환된 PostListResponse 객체
     */
    public static PostListResponseDto from(CommunityPost post) {
        return new PostListResponseDto(
                post.getPostIdx(),
                post.getTitle(),
                post.getUser().getNickname(),
                post.getCategory(),
                post.getCreatedAt(),
                post.getViews(),
                post.getLikeCount(),
                post.getStatus(),
                post.getContent(),
                post.getUser().getProfileImg(),
                post.getCommentCount()
        );
    }
}