package io.github.sunday.devfolio.entity.table.community;

/**
 * 커뮤니티 게시글의 카테고리를 정의한 열거형입니다.
 * <ul>
 *   <li>study</li>
 *   <li>question</li>
 *   <li>general</li>
 * </ul>
 */
public enum Category {
    study,
    question,
    general;

    public static boolean isValid(String value) {
        if (value == null) return false;
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