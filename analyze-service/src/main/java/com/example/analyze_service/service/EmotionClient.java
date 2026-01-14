package com.example.analyze_service.service;

import com.example.analyze_service.util.StandardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "emotion-service")
public interface EmotionClient {
    // Note: You might need to add a "get-latest-emotion" endpoint to your EmotionController
    @GetMapping("/api/v1/emotion/latest")
    ResponseEntity<StandardResponse> getLatestEmotion(@RequestHeader("X-User-Id") int userId);
}