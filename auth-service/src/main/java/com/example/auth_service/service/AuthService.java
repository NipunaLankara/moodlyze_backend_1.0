package com.example.auth_service.service;

import com.example.auth_service.dto.request.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthService extends UserDetailsService {
    String saveNewUser(@Valid UserSaveDTO userSaveDTO) throws JsonProcessingException;

    String verifyOtpSaveUser(@Valid OtpVerifyDTO otpVerifyDTO) throws JsonProcessingException;

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    Object createJwtTokenAndLogin(LoginRequestDTO dto) throws Exception;

    String requestEmailChange(int userId, EmailChangeRequestDTO emailChangeRequestDTO) throws JsonProcessingException;

    String verifyEmailChange(int userId, OtpVerifyDTO dto);

    String deleteAuthUser(String email);

    String set2fa(int userId, boolean status);

    Object verify2faAndLogin(OtpVerifyDTO dto);

    String requestPasswordReset(@NotBlank(message = "Email is required") @Email(message = "Invalid Email Format") String email);

    String verifyAndResetPassword(ResetPasswordDTO dto);
}
