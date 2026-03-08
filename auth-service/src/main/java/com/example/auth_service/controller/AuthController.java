package com.example.auth_service.controller;

import com.example.auth_service.dto.request.*;
import com.example.auth_service.service.AuthService;
import com.example.auth_service.util.StandardResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")

public class AuthController {
    @Autowired
    @Lazy
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/sign-up")
    public ResponseEntity<StandardResponse> registerNewUser(@Valid @RequestBody UserSaveDTO userSaveDTO) throws JsonProcessingException {

        String message = authService.saveNewUser(userSaveDTO);

        return new ResponseEntity<>(
                new StandardResponse(200, "Success", message), HttpStatus.OK
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<StandardResponse> verifyOtp(
            @Valid @RequestBody OtpVerifyDTO otpVerifyDTO) throws JsonProcessingException {

        String message = authService.verifyOtpSaveUser(otpVerifyDTO);

        return new ResponseEntity<>(
                new StandardResponse(200, "OTP Verification", message),
                HttpStatus.OK
        );
    }

    @PostMapping("/sign-in")
    public ResponseEntity<StandardResponse> login(@RequestBody LoginRequestDTO dto) throws Exception {

        Object response = authService.createJwtTokenAndLogin(dto);
        return new ResponseEntity<>(
                new StandardResponse(200, "Login request processed", response),
                HttpStatus.OK
        );
    }

    @PostMapping("/email-change/request")
    public ResponseEntity<StandardResponse> requestEmailChange(
            @RequestParam("userId") int userId,
            @RequestBody EmailChangeRequestDTO emailChangeRequestDTO
    ) throws JsonProcessingException {

        String msg = authService.requestEmailChange(userId,emailChangeRequestDTO);

        return new ResponseEntity<>(
                new StandardResponse(200, "Success", msg),
                HttpStatus.OK
        );
    }

    @PostMapping("/email-change/verify")
    public ResponseEntity<?> verifyEmailChange(
            @RequestHeader("X-User-Id") int userId,
            @RequestBody OtpVerifyDTO dto
    ) {

        String msg = authService.verifyEmailChange(userId,dto);

        return new ResponseEntity<>(
                new StandardResponse(200, "Success", msg),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/internal/delete")
    public ResponseEntity<StandardResponse> deleteAuthUser(@RequestParam("email") String email) {

        String msg = authService.deleteAuthUser(email);

        return new ResponseEntity<>(
                new StandardResponse(200, "Success", msg),
                HttpStatus.OK
        );

    }

    @PutMapping("/2fa/enable-disable/{id}")
    public ResponseEntity<StandardResponse> enableOrDisable2FA (
            @RequestHeader("X-User-Id") int userId,
            @RequestParam (value = "status") boolean status
    ) {
        String message = authService.set2fa(userId,status);
        return new ResponseEntity<>(
                new StandardResponse(200, "2FA Changed", message),
                HttpStatus.OK
        );

    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<StandardResponse> verify2fa(@RequestBody OtpVerifyDTO dto) {

        System.out.println(dto.getOtp());
        Object response = authService.verify2faAndLogin(dto);

        return new ResponseEntity<>(
                new StandardResponse(200, "2FA verification successful", response),
                HttpStatus.OK
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<StandardResponse> forgotPassword(@RequestBody @Valid ResetPasswordDTO passwordRestRequestDTO) {
        String result = authService.requestPasswordReset(passwordRestRequestDTO.getEmail());

        return new ResponseEntity<>(
                new StandardResponse(200,"Success",result),HttpStatus.OK
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<StandardResponse> resetPassword(
            @RequestBody ResetPasswordDTO dto) {

        String msg = authService.verifyAndResetPassword(dto);

        return new ResponseEntity<>(
                new StandardResponse(200, "Success", msg),
                HttpStatus.OK
        );
    }
}
