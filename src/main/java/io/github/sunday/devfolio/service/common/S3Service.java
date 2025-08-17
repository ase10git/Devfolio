package io.github.sunday.devfolio.service.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

/**
 * Amazon AWS S3 파일 관리를 위한 서비스
 */
@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;

    /**
     * AWS S3 버킷 연결
     */
    public S3Service(
            @Value("${aws.access-key-id}") String accessKey,
            @Value("${aws.secret-access-key}") String secretKey,
            @Value("${aws.region}") String region,
            @Value("${aws.s3.bucket-name}") String bucketName) {

        this.bucketName = bucketName;

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    /**
     * 파일 업로드
     */
    public String uploadFile(MultipartFile file, String fileFullPath) throws IOException {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileFullPath)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        return getFileUrl(fileFullPath);
    }

    /**
     * 파일 URL 생성하기
     */
    public String getFileUrl(String fileName) {
        S3Utilities s3Utilities = s3Client.utilities();
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return s3Utilities.getUrl(request).toString();
    }

    /**
     * 파일 제거하기
     */
    public void deleteFile(String fileName) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(request);
    }
}
