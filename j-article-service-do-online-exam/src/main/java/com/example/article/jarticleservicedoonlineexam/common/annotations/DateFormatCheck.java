package com.example.article.jarticleservicedoonlineexam.common.annotations;

@java.lang.annotation.Documented
@jakarta.validation.Constraint(validatedBy = DateFormatCheckConstraintValidator.class)
@java.lang.annotation.Target({
	java.lang.annotation.ElementType.METHOD,
	java.lang.annotation.ElementType.FIELD,
	java.lang.annotation.ElementType.ANNOTATION_TYPE,
	java.lang.annotation.ElementType.CONSTRUCTOR,
	java.lang.annotation.ElementType.PARAMETER,
	java.lang.annotation.ElementType.TYPE_USE
})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface DateFormatCheck {
	String pattern() default "yyyy-MM-dd HH:mm:ss.ffffff";

	boolean isChecked() default false;

	String message() default "Not match date pattern";

	Class<?>[] groups() default {};

	Class<? extends jakarta.validation.Payload>[] payload() default {};
}
