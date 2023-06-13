package com.example.demo.common.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class CheckListSizeConstraintValidator
		implements ConstraintValidator<CheckListSize, List<Long>> {
	@Override
	public void initialize(CheckListSize constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(List<Long> input, ConstraintValidatorContext constraintValidatorContext) {
		return input.size() > 0;
	}
}
