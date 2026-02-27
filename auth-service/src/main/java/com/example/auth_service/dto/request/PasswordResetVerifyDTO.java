package com.example.auth_service.dto.request;

import com.example.auth_service.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordResetVerifyDTO extends OtpVerifyDTO{

    @ValidPassword
    private String newPassword;
}
