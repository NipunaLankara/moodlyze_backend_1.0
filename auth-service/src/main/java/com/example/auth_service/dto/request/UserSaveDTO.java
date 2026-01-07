package com.example.auth_service.dto.request;

import com.example.auth_service.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSaveDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name length should be 2-50")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @ValidPassword
    private String password;

    @NotBlank(message = "User role is required")
    private String userRole;  // ADMIN / ASSISTANT / CUSTOMER

    public UserSaveDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
