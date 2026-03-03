package com.example.emotion_service.service;

import com.example.emotion_service.config.FeignJsonConfig;
import com.example.emotion_service.dto.EmotionResponseDTO;
import com.example.emotion_service.dto.TextEmotionRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "ai-service",
        url = "http://localhost:8091",
        configuration = FeignJsonConfig.class   // 👈 important
)
public interface AiServiceClient {

    @PostMapping(
            value = "/api/v1/ai/emotion",
            consumes = "application/json",
            produces = "application/json"
    )
    EmotionResponseDTO getAiResponse(@RequestBody TextEmotionRequestDTO request);
}