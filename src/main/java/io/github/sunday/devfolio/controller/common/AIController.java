package io.github.sunday.devfolio.controller.common;

import io.github.sunday.devfolio.config.CustomUserDetails;
import io.github.sunday.devfolio.dto.portfolio.PortfolioTemplateRequestDto;
import io.github.sunday.devfolio.service.common.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @Valid @ModelAttribute PortfolioTemplateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            if (userDetails == null || userDetails.getUser() == null) {
                throw new Exception("로그인이 필요합니다.");
            }
            return aiService.getPortfolioTemplate(requestDto.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("내부 서버 에러가 발생했습니다.");
        }
    }
}
