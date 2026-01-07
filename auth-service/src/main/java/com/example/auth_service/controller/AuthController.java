package com.example.auth_service.controller;

import com.example.auth_service.dto.request.LoginRequestDTO;
import com.example.auth_service.dto.request.OtpVerifyDTO;
import com.example.auth_service.dto.request.UserSaveDTO;
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
@CrossOrigin

public class AuthController {
    @Autowired
    @Lazy
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/sign-up")
    public ResponseEntity<StandardResponse> registerNewUser(@Valid @RequestBody UserSaveDTO userSaveDTO) throws JsonProcessingException {

        String message = authService.saveNewUser(userSaveDTO);

        return  new ResponseEntity<>(
                new StandardResponse(200,"Success",message), HttpStatus.OK
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<StandardResponse> verifyOtp(
            @Valid @RequestBody OtpVerifyDTO otpVerifyDTO) throws JsonProcessingException {

        String message = authService.verifyOtp(otpVerifyDTO);

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

}
