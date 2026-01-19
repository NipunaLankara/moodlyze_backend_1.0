package com.example.auth_service.service;

import com.example.auth_service.dto.request.UserSaveDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service")
public interface UserApiClient {

    @PostMapping("/api/v1/user/save")
    public boolean save(@RequestBody UserSaveDTO userSaveDTO);

    @GetMapping("/api/v1/user/get-role")
    String getRole(@RequestParam("email") String email); // Added @RequestParam here too

    @GetMapping("/api/v1/user/get-id")
    Integer getUserId(@RequestParam("email") String email); // Added @RequestParam here too


    @PutMapping("api/v1/user/update-email")
    void updateEmail(
            @RequestParam("oldEmail") String oldEmail,
            @RequestParam("newEmail") String newEmail
    );

}
