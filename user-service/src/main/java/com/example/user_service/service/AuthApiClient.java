package com.example.user_service.service;

import com.example.user_service.dto.request.EmailChangeRequestDTO;
import com.example.user_service.utill.StandardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service")
public interface AuthApiClient {

    @PostMapping("/api/v1/auth/email-change/request")
    StandardResponse requestEmailChange(
            @RequestParam("userId") int userId,
            @RequestBody EmailChangeRequestDTO emailChangeRequestDTO
    );

    @DeleteMapping("/api/v1/auth/internal/delete")
    StandardResponse  deleteAuthUser(@RequestParam("email") String email);

}
