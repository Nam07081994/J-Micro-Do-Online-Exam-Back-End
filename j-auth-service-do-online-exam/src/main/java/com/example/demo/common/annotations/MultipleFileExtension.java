package com.example.demo.common.annotations;

@java.lang.annotation.Documented
@jakarta.validation.Constraint(validatedBy = MultipleFileExtensionConstraintValidator.class)
@java.lang.annotation.Target({
	java.lang.annotation.ElementType.METHOD,
	java.lang.annotation.ElementType.FIELD,
	java.lang.annotation.ElementType.ANNOTATION_TYPE,
	java.lang.annotation.ElementType.CONSTRUCTOR,
	java.lang.annotation.ElementType.PARAMETER,
	java.lang.annotation.ElementType.TYPE_USE
})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface MultipleFileExtension {
	String[] allowedTypes() default {"png", "jpg", "jpeg", "gif"};

	String message() default "MultipleFile extensions are not valid!!";

	Class<?>[] groups() default {};

	Class<? extends jakarta.validation.Payload>[] payload() default {};
}
