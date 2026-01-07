package com.example.user_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)  // Link to our validator class
@Target({ ElementType.FIELD })                      // Can only be used on fields
@Retention(RetentionPolicy.RUNTIME)                 // Available at runtime
public @interface ValidPassword {
    String message() default "Invalid Password";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
