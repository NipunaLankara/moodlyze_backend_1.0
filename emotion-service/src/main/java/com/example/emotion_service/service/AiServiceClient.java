package com.example.emotion_service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "ai-service",
        url = "http://localhost:8091"
)
public interface AiServiceClient {
    @PostMapping("/api/v1/ai/generate")
    String getAiResponse(@RequestBody String prompt);
}