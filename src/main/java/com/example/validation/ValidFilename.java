package com.example.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidFilenameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFilename {
    String message() default "Filename cannot contain forward slashes";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 