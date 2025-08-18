package io.github.sunday.devfolio.controller.common;

import io.github.sunday.devfolio.dto.common.ImageUploadResult;
import io.github.sunday.devfolio.service.common.SecureImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EditorImageController {
    private final SecureImageService secureImageService;

    @PostMapping("/image/upload")
    public ResponseEntity<Map<String, Object>> imageUpload(MultipartRequest request) {
        Map<String, Object> responseData = new HashMap<>();
        String filePath = "temp";
        MultipartFile image = request.getFile("upload");
        try {
            ImageUploadResult uploadResult = secureImageService.uploadImage(image, filePath);
            responseData.put("uploaded", true);
            responseData.put("url", uploadResult.getImageUrl());
        } catch (Exception e) {
            responseData.put("error", "image upload failed");
            return ResponseEntity.badRequest().body(responseData);
        }
        return ResponseEntity.ok(responseData);
    }
}
