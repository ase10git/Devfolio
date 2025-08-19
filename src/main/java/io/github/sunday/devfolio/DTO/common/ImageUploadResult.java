package io.github.sunday.devfolio.dto.common;

import lombok.*;

import java.time.ZonedDateTime;

/**
 * AWS S3 이미지 업로드 결과 처리용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageUploadResult {

    /**
     * 원본 파일 이름
     */
    private String originalFileName;

    /**
     * AWS S3 Key
     */
    private String s3Key;

    /**
     * 이미지 URL
     */
    private String imageUrl;

    /**
     * 파일 크기
     */
    private Long fileSize;

    /**
     * 업로드 날짜
     */
    private ZonedDateTime uploadedAt;
}
