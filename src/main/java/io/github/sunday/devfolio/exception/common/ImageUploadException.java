package io.github.sunday.devfolio.exception.common;

/**
 * 이미지 업로드 실패 처리용 예외
 */
public class ImageUploadException extends Exception {
    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
