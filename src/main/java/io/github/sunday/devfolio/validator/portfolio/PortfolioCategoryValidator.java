package io.github.sunday.devfolio.validator.portfolio;

import io.github.sunday.devfolio.annotation.portfolio.PortfolioCategoryValid;
import io.github.sunday.devfolio.service.portfolio.PortfolioCategoryService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 포트폴리오 카테고리 검증을 위한 Validator
 */
@Component
@RequiredArgsConstructor
public class PortfolioCategoryValidator implements ConstraintValidator<PortfolioCategoryValid, String> {
    private final PortfolioCategoryService portfolioCategoryService;

    /**
     * 포트폴리오 카테고리 서비스의 메서드로 값 검증 진행
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return portfolioCategoryService.exists(value);
    }
}
