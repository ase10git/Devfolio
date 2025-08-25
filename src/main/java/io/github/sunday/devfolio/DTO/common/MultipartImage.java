package io.github.sunday.devfolio.dto.common;

import lombok.*;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * BufferedImage를 MultipartFile로 변환할 때 사용하는 클래스
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MultipartImage implements MultipartFile {

    /**
     * 바이트 배열
     */
    private byte[] bytes;

    /**
     * 이름
     */
    String name;

    /**
     * 원본 파일 이름
     */
    String originalFilename;

    /**
     * 파일 타입
     */
    String contentType;

    /**
     * 파일이 비어있는지 여부
     */
    boolean isEmpty;

    /**
     * 파일 크기
     */
    long size;

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public Resource getResource() {
        return MultipartFile.super.getResource();
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        MultipartFile.super.transferTo(dest);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
    }
}
