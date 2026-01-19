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

//    @PostMapping("/email-change/request")
//    StandardResponse requestEmailChange(
//            @RequestParam("oldEmail") String oldEmail,
//            @RequestParam("newEmail") String newEmail
//    );

    @PostMapping("/api/v1/auth/email-change/request")
    StandardResponse requestEmailChange(@RequestBody EmailChangeRequestDTO emailChangeRequestDTO);

    // Delete auth user (internal)
    @DeleteMapping("/api/v1/auth/internal/delete")
    public void deleteAuthUser(
            @RequestParam("email") String email
    );

}
