package io.github.sunday.devfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// Todo : Spring Security 설정 완료 후 제거
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class DevfolioApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevfolioApplication.class, args);
	}

}
