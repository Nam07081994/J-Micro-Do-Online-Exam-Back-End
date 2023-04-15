package com.example.demo.common.annotations;

import com.example.demo.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailUniqueConstraintValidator implements ConstraintValidator<EmailUnique, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(EmailUnique constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        Boolean a = userRepository.findByEmail(s)
                .isEmpty();
        return userRepository.findByEmail(s)
                .isEmpty();
    }
}
