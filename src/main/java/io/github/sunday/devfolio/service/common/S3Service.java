package io.github.sunday.devfolio.service.common;

import org.apache.commons.io.FilenameUtils;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public String uploadFile(MultipartFile file, String fileFullPath, boolean isTemp) throws IOException {
        String contentType = "image/" + FilenameUtils.getExtension(fileFullPath);
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileFullPath)
                .contentType(contentType)
                .tagging(isTemp ? "lifecycle=TEMP" : null)
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

    public void updateObjectTags(String fileName, String newTagKey, String newTagValue) {
        // 객체의 현재 태그 가져오기
        GetObjectTaggingRequest getTaggingRequest = GetObjectTaggingRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        GetObjectTaggingResponse getTaggingResponse = s3Client.getObjectTagging(getTaggingRequest);

        // 기존 태그 목록 가져오기
        List<Tag> tags = new ArrayList<>(getTaggingResponse.tagSet());

        // 새로운 태그 추가 또는 기존 태그 업데이트
        boolean tagUpdated = false;
        for (Tag tag : tags) {
            if (tag.key().equals(newTagKey)) {
                tags.remove(tag);
                tags.add(Tag.builder().key(newTagKey).value(newTagValue).build());
                tagUpdated = true;
                break;
            }
        }
        if (!tagUpdated) {
            tags.add(Tag.builder().key(newTagKey).value(newTagValue).build());
        }

        // 새로운 태그 세트로 객체 태그 업데이트
        PutObjectTaggingRequest putTaggingRequest = PutObjectTaggingRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .tagging(Tagging.builder().tagSet(tags).build())
                .build();

        // 응답 로그 출력
        PutObjectTaggingResponse response = s3Client.putObjectTagging(putTaggingRequest);
    }

}
