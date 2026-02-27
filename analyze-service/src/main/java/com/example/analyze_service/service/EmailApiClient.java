package com.example.analyze_service.service;

import com.example.analyze_service.dto.EmailRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "email-service")
public interface EmailApiClient {

    @PostMapping("/api/v1/email/send")
    void sendEmail(EmailRequestDTO request);

}