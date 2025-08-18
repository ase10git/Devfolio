package io.github.sunday.devfolio.config;

import io.jsonwebtoken.Jwt;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProps {

    private Jwt jwt;

    @Data
    public static class Jwt {
        private long accessTtlMs;
        private long refreshTtlMs;
        private String secret;
        private String refreshPepper;
    }
}