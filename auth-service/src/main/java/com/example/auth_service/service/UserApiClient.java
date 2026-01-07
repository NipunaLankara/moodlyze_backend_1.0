package com.example.auth_service.service;

import com.example.auth_service.dto.request.UserSaveDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserApiClient {

    @PostMapping("/api/v1/user/save")
    public boolean save(@RequestBody UserSaveDTO userSaveDTO);

    @GetMapping("/api/v1/user/get-role")
    String getRole(@RequestParam("email") String email); // Added @RequestParam here too
}
