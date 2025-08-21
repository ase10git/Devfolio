package io.github.sunday.devfolio.annotation.community;

import io.github.sunday.devfolio.validator.community.CommunityCategoryValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 게시글 카테고리 검증을 위한 커스텀 어노테이션
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CommunityCategoryValidator.class)
@Documented
public @interface CommunityCategoryValid {
    String message() default "유효하지 않은 포트폴리오 카테고리입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
