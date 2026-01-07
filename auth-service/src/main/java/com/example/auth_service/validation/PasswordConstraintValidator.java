package com.example.auth_service.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword,String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.trim().isEmpty()) {
            buildMessage(context, "Password cannot be empty");
            return false;
        }

        if (password.length() < 5) {
            buildMessage(context, "Enter at least 5 characters");
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            buildMessage(context, "Password must contain at least one uppercase letter");
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            buildMessage(context, "Password must contain at least one number");
            return false;
        }

        return true;
    }

    private void buildMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

}
