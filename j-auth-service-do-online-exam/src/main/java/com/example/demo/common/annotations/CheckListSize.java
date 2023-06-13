package com.example.demo.common.annotations;

@java.lang.annotation.Documented
@jakarta.validation.Constraint(validatedBy = CheckListSizeConstraintValidator.class)
@java.lang.annotation.Target({
	java.lang.annotation.ElementType.METHOD,
	java.lang.annotation.ElementType.FIELD,
	java.lang.annotation.ElementType.ANNOTATION_TYPE,
	java.lang.annotation.ElementType.CONSTRUCTOR,
	java.lang.annotation.ElementType.PARAMETER,
	java.lang.annotation.ElementType.TYPE_USE
})
public @interface CheckListSize {
	String message() default "Invalid list input";

	Class<?>[] groups() default {};

	Class<? extends jakarta.validation.Payload>[] payload() default {};
}
