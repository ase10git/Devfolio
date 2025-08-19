package io.github.sunday.devfolio.validator.portfolio;

import io.github.sunday.devfolio.annotation.portfolio.PortfolioCategoryValid;
import io.github.sunday.devfolio.service.portfolio.PortfolioCategoryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

        if (value instanceof Long id) {
            return portfolioCategoryService.exists(id);
        }

        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(Long.class::isInstance)
                    .map(Long.class::cast)
                    .allMatch(portfolioCategoryService::exists);
        }
        return false;
    }
}
