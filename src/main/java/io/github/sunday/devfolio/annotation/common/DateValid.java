package io.github.sunday.devfolio.annotation.common;

import io.github.sunday.devfolio.validator.common.DateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 날짜 String 형식이 yyyy-MM-dd 형식이면서 실제 날짜인지 검증하는 커스텀 어노테이션
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
@Documented
public @interface DateValid {
    String message() default "올바른 날짜 형식이 아니거나 존재하지 않는 날짜입니다. (yyyy-MM-dd)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
