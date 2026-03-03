package com.example.analyze_service.service;

import com.example.analyze_service.dto.ActivityResponseDTO;
import com.example.analyze_service.dto.SuggestionsRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-service")
public interface AiServiceClient {
//    @PostMapping("/api/v1/ai/generate")
//    String generate(@RequestBody String prompt);

    @PostMapping("/api/v1/ai/activities")
    ActivityResponseDTO getActivities(@RequestBody SuggestionsRequestDTO requestDTO);

}