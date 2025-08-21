package io.github.sunday.devfolio.controller.common;

import io.github.sunday.devfolio.service.common.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * AI 서비스 요청용 컨트롤러
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiService aiService;

    /**
     * Alan AI로 포트폴리오 작성 템플릿 요청하기
     */
    @GetMapping("/portfolio-template")
    public ResponseEntity<String> getPortfolioTemplate(
            @RequestParam String type
    ) {
        try {
            String response = aiService.getPortfolioTemplate(type);
            aiService.resetState();
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("내부 서버 에러가 발생했습니다.");
        }
    }
}
