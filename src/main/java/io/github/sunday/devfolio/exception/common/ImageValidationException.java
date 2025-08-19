package io.github.sunday.devfolio.exception.common;

import java.io.IOException;

/**
 * 이미지 유효성 검사 처리용 예외
 */
public class ImageValidationException extends IOException {
    public ImageValidationException(String message) {
        super(message);
    }
}
