package io.github.sunday.devfolio.controller.common;

import io.github.sunday.devfolio.service.common.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 서비스 요청용 컨트롤러
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {
    private final AIService aiService;

    /**
     * Alan AI로 포트폴리오 작성 템플릿 요청하기
     */
    @GetMapping("/portfolio-template")
    public ResponseEntity<?> getPortfolioTemplate(
            @RequestParam String type
    ) {
        try {
            return aiService.getPortfolioTemplate(type);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("내부 서버 에러가 발생했습니다.");
        }
    }
}
