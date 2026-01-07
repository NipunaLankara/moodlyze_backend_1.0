package com.example.auth_service.dto.request;

import com.example.auth_service.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthUserSaveDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @ValidPassword
    private String password;


}
