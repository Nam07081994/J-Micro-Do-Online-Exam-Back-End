package com.example.demo.common.anotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatCheckConstraintValidator
		implements ConstraintValidator<DateFormatCheck, String> {

	private String pattern;

	private boolean isChecked;

	@Override
	public void initialize(DateFormatCheck constraintAnnotation) {
		this.isChecked = constraintAnnotation.isChecked();
		this.pattern = constraintAnnotation.pattern();
	}

	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		if (isChecked) {
			try {
				LocalDateTime.parse(s, formatter);
			} catch (Exception ex) {
				return false;
			}
		}

		return true;
	}
}
