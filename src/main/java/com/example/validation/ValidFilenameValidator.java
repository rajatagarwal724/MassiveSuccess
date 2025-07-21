package com.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidFilenameValidator implements ConstraintValidator<ValidFilename, String> {
    
    @Override
    public void initialize(ValidFilename constraintAnnotation) {
    }

    @Override
    public boolean isValid(String filename, ConstraintValidatorContext context) {
        if (filename == null) {
            return true; // Let @NotNull handle null validation
        }
        return !filename.contains("/");
    }
} 