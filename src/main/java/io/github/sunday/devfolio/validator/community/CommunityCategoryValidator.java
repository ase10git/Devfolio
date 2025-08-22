package io.github.sunday.devfolio.validator.community;

import io.github.sunday.devfolio.annotation.community.CommunityCategoryValid;
import io.github.sunday.devfolio.entity.table.community.Category;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 게시글 카테고리 검증을 위한 Validator
 */
@Component
@RequiredArgsConstructor
public class CommunityCategoryValidator implements ConstraintValidator<CommunityCategoryValid, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return Category.isValid(value);
    }
}
