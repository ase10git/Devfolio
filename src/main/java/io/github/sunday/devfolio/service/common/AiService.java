package io.github.sunday.devfolio.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

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
    public String getPortfolioTemplate(String type) {
        return alanClient.get()
                .uri(buildRequestUrl(type))
                .retrieve()
                .body(String.class);
    }

    /**
     * Alan AI 요청용 URL 생성
     */
    private String buildRequestUrl(String type) {
        String content = "개발자 프로젝트에 대한 포트폴리오를 작성하고 있어." + type + " 프로젝트를 진행했을 때 자주 사용하는 목차를 추천해줘.";
        return UriComponentsBuilder.fromPath(alanUrlQuestion)
                .queryParam("content", content)
                .queryParam("client_id", alanApiKey)
                .encode()
                .build().toUriString();
    }
}
