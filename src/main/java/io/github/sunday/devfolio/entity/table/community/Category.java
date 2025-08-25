package io.github.sunday.devfolio.entity.table.community;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 커뮤니티 게시글의 카테고리를 정의한 열거형입니다.
 * <ul>
 *   <li>study</li>
 *   <li>question</li>
 *   <li>general</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public enum Category {
    study("스터디 그룹 모집"),
    question("질문 & 답변"),
    general("자유 게시판");

    private final String nameKo;

    public static boolean isValid(String value) {
        if (value == null) return true;
        return java.util.Arrays.stream(Category.values())
                .anyMatch(c -> c.name().equals(value));
    }

    public static Category getCategory(String value) {
        return java.util.Arrays.stream(Category.values())
                .filter(c -> c.name().equals(value))
                .findFirst()
                .orElse(null);
    }
}