package com.example.reminder_service.client;


import com.example.reminder_service.dto.request.EmailRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "email-service")
public interface EmailApiClient {

    @PostMapping("/api/v1/email/send")
    void sendEmail(EmailRequestDTO request);
}