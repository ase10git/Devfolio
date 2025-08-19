package io.github.sunday.devfolio.controller.common;

import io.github.sunday.devfolio.dto.common.ImageUploadResult;
import io.github.sunday.devfolio.service.common.SecureImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * CKEditor 이미지 업로드 관리 Controller
 */
@RestController
@RequiredArgsConstructor
public class EditorImageController {
    private final SecureImageService secureImageService;

    @PostMapping("/image/upload")
    public ResponseEntity<Map<String, Object>> imageUpload(
            @RequestParam String target,
            MultipartRequest request
    ) {
        Map<String, Object> responseData = new HashMap<>();
        try {
            // Todo : 사용자 IDX 가져오기
            Long userIdx = 1L;
            MultipartFile image = request.getFile("upload");
            ImageUploadResult uploadResult = secureImageService.uploadTempImage(image, target, userIdx);
            responseData.put("uploaded", true);
            responseData.put("url", uploadResult.getImageUrl());
        } catch (Exception e) {
            responseData.put("error", "image upload failed");
            return ResponseEntity.badRequest().body(responseData);
        }
        return ResponseEntity.ok(responseData);
    }
}
