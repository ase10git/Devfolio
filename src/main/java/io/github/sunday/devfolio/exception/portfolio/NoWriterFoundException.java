package io.github.sunday.devfolio.exception.portfolio;

/**
 * 작성자가 없을 때의 예외 처리
 */
public class NoWriterFoundException extends Exception {
    public NoWriterFoundException(String messsage) {
        super(messsage);
    }
}
