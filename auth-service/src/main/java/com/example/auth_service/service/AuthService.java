package com.example.auth_service.service;

import com.example.auth_service.dto.request.LoginRequestDTO;
import com.example.auth_service.dto.request.OtpVerifyDTO;
import com.example.auth_service.dto.request.UserSaveDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.rmi.AlreadyBoundException;

public interface AuthService extends UserDetailsService {
    String saveNewUser(@Valid UserSaveDTO userSaveDTO) throws JsonProcessingException;

    String verifyOtp(@Valid OtpVerifyDTO otpVerifyDTO) throws JsonProcessingException;

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    Object createJwtTokenAndLogin(LoginRequestDTO dto) throws Exception;
}
