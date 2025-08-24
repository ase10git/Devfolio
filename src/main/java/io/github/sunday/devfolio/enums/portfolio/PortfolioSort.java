package io.github.sunday.devfolio.enums.portfolio;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 포트폴리오 정렬 기준을 설정한 ENUM
 */
@Getter
@RequiredArgsConstructor
public enum PortfolioSort {
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
     * 필드 이름으로 PortfolioSort 탐색
     */
    public static PortfolioSort fromName(String name) {
        for (PortfolioSort sort : values()) {
            if (sort.name().equalsIgnoreCase(name))
                return sort;
        }
        return null;
    }
}
