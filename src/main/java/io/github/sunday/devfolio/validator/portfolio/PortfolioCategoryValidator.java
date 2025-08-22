package io.github.sunday.devfolio.validator.portfolio;

import io.github.sunday.devfolio.annotation.portfolio.PortfolioCategoryValid;
import io.github.sunday.devfolio.service.portfolio.PortfolioCategoryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 포트폴리오 카테고리 검증을 위한 Validator
 */
@Component
@RequiredArgsConstructor
public class PortfolioCategoryValidator implements ConstraintValidator<PortfolioCategoryValid, Object> {
    private final PortfolioCategoryService portfolioCategoryService;

    /**
     * 포트폴리오 카테고리 서비스의 메서드로 값 검증 진행
     * 단일 Long id와 리스트 Long 모두 검증 가능
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        // 포트폴리오 id로 요청이 들어오는 경우
        if (value instanceof Long id) {
            return portfolioCategoryService.exists(id);
        }

        // 포트폴리오 id 리스트로 요청이 들어오는 경우
        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(Long.class::isInstance)
                    .map(Long.class::cast)
                    .allMatch(portfolioCategoryService::exists);
        }

        // 포트폴리오 String으로 요청이 들어오는 경우
        if (value instanceof String name) {
            if (name.isEmpty()) return false;

            String[] categories = name.split("\\s*,\\s*");
            return Arrays.stream(categories)
                    .allMatch(categoryName -> portfolioCategoryService.existsByName(categoryName));
        }

        return false;
    }
}
