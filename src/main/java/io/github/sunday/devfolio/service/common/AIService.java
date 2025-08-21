package io.github.sunday.devfolio.service.common;

import io.github.sunday.devfolio.dto.common.AlanResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
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
public class AIService {

    @Value("${alan.api.url.base}")
    private String baseUrl;

    @Value("${alan.api.url.question}")
    private String alanUrlQuestion;

    @Value("${alan.api.url.reset}")
    private String alanUrlReset;

    @Value("${alan.api.key}")
    private String alanApiKey;

    private final RestClient alanClient;
    private final RestTemplate alanRestTemplate;

    /**
     * 포트폴리오 템플릿 추천 받기 
     */
    public AlanResponseDto sendAIRequest(String type) throws Exception {
        return alanClient.get()
                .uri(buildPortfolioRequestUrl(type))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), (req, res) -> {
                    throw new RestClientResponseException(
                            "클라이언트 오류 발생: " + res.getStatusCode(),
                            res.getStatusCode().value(),
                            res.getStatusText(),
                            null,
                            res.getBody().readAllBytes(),
                            null
                    );
                })
                .onStatus(status -> status.is5xxServerError(), (req, res) -> {
                    throw new RestClientResponseException(
                            "서버 오류 발생: " + res.getStatusCode(),
                            res.getStatusCode().value(),
                            res.getStatusText(),
                            null,
                            res.getBody().readAllBytes(),
                            null
                    );
                })
                .body(AlanResponseDto.class);
    }

    public ResponseEntity<?> getPortfolioTemplate(String type) throws Exception {
        if (type == null || type.isEmpty()) return ResponseEntity.badRequest().body("카테고리 타입을 입력해주세요");

        try {
            AlanResponseDto response = sendAIRequest(type);

            if (response != null && !response.getContent().isEmpty()) {
                ResponseEntity<?> deleteResponse = resetState();
                if (!deleteResponse.getStatusCode().equals(HttpStatus.OK)) {
                    System.out.println(deleteResponse.toString());
                }
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.internalServerError().body("Alan AI 응답을 받아오는 데 오류가 발생했습니다");
        } catch (RestClientResponseException ex) {
            System.err.println("API 요청 실패: " + ex.getRawStatusCode() + " - " + ex.getResponseBodyAsString());
            throw ex;
        }
    }

    /**
     * 상태 초기화
     */
    public ResponseEntity<?> resetState() {
        String url = baseUrl + alanUrlReset;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");

        String body = String.format("{\"client_id\": \"%s\"}", alanApiKey);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        return alanRestTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
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
}
