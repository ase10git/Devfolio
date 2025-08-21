package io.github.sunday.devfolio.config;

import io.github.sunday.devfolio.interceptor.LoginUserInterceptor;
import io.github.sunday.devfolio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final UserService userService;

    @Autowired
    public WebConfig(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginUserInterceptor(userService))
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/assets/**", "/ckeditor5/**", "/prompts/**");
    }
}
