package io.github.sunday.devfolio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * HTTP 요청용 RestClient
 */
@Configuration
public class RestClientConfig {

    /**
     * Alan AI 요청용 RestClient 빈 등록 
     */
    @Bean
    public RestClient alanClient(
            @Value("${alan.api.url.base}") String baseUrl
    ) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(100);
        factory.setConnectionRequestTimeout(70);

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
