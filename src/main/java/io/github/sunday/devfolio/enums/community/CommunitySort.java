package io.github.sunday.devfolio.enums.community;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 커뮤니티 글의 정렬 기준
 */
@Getter
@RequiredArgsConstructor
public enum CommunitySort {
    /**
     * 수정 날짜, 댓글 수, 조회수, 좋아요 수
     */
    UPDATED_AT("updatedAt", "최신순"),
    COMMENT_COUNT("commentCount", "댓글수"),
    VIEWS("views", "조회수"),
    LIKE_COUNT("likeCount", "좋아요수");

    private final String fieldName;
    private final String fieldNameKo;

    /**
     * 필드 이름으로 CommunitySort 탐색
     */
    public static CommunitySort fromName(String name) {
        for (CommunitySort sort : values()) {
            if (sort.name().equalsIgnoreCase(name))
                return sort;
        }
        return null;
    }
}
