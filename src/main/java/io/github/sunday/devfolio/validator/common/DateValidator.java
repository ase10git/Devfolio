package io.github.sunday.devfolio.validator.common;

import io.github.sunday.devfolio.annotation.common.DateValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 날짜 형식을 검증하는 Validator
 * yyyy-MM-dd 형식을 검증
 * 실제 존재하는 날짜인지 검증(예: 윤년 등)
 */
public class DateValidator implements ConstraintValidator<DateValid, String> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        try {
            LocalDate.parse(value, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
