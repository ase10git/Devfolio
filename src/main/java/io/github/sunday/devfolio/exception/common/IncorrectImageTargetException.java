package io.github.sunday.devfolio.exception.common;

/**
 * CKEditor로 이미지 업로드 요청 시 이미지 저장 대상 처리용 예외
 */
public class IncorrectImageTargetException extends IllegalArgumentException{
    public IncorrectImageTargetException(String message) {
        super(message);
    }
}
