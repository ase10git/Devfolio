package io.github.sunday.devfolio.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * AI 서비스
 */
@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${alan.api.url.question}")
    private String alanUrlQuestion;

    @Value("${alan.api.url.reset}")
    private String alanUrlReset;

    @Value("${alan.api.key}")
    private String alanApiKey;

    private final RestClient alanClient;

    /**
     * 포트폴리오 템플릿 추천 받기 
     */
    public String getPortfolioTemplate(String type) throws IOException {
        return alanClient.get()
                .uri(buildPortfolioRequestUrl(type))
                .retrieve()
                .body(String.class);
    }

    /**
     * 상태 초기화
     */
    public ResponseEntity<Void> resetState() {
        return alanClient.delete()
                .uri(buildAlanResetUrl())
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Alan AI 요청용 URL 생성
     */
    private String buildPortfolioRequestUrl(String type) throws IOException {
        Path path = new ClassPathResource("/static/prompts/portfolio_template.txt").getFile().toPath();
        String template = Files.readString(path);
        String convert = new String(template.getBytes(StandardCharsets.UTF_8));
        String prompt = String.format(convert, type);
        return UriComponentsBuilder.fromPath(alanUrlQuestion)
                .queryParam("content", prompt)
                .queryParam("client_id", alanApiKey)
                .encode()
                .build().toUriString();
    }

    /**
     * Alan AI 리셋용 URL 생성
     */
    private String buildAlanResetUrl() {
        return UriComponentsBuilder.fromPath(alanUrlReset)
                .queryParam("client_id", alanApiKey)
                .encode()
                .build().toUriString();
    }
}
