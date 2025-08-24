package io.github.sunday.devfolio.enums.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 이미지 대상 ENUM
 */
@Getter
@RequiredArgsConstructor
public enum ImageTarget {
    PROFILE("profile"),
    PORTFOLIO("portfolio"),
    COMMUNITY("community");

    private final String targetName;

    public static ImageTarget fromFieldName(String fieldName) {
        for (ImageTarget target : values()) {
            if (target.getTargetName().equalsIgnoreCase(fieldName)) {
                return target;
            }
        }
        return null;
    }
}
