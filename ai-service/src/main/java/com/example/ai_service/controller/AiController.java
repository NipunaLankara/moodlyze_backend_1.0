package com.example.ai_service.controller;

import com.example.ai_service.dto.EmotionResponseDTO;
import com.example.ai_service.dto.ActivityResponseDTO;
import com.example.ai_service.dto.AiRequestDTO;
import com.example.ai_service.dto.SuggestionsRequestDTO;
import com.example.ai_service.dto.TextEmotionRequestDTO;
import com.example.ai_service.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin
public class AiController {

    @Autowired
    private AiService aiService;

    // Detect emotion
    @PostMapping("/emotion")
    public EmotionResponseDTO detectEmotion(@RequestBody TextEmotionRequestDTO request) {
        System.out.println(request);
        return aiService.detectEmotion(request.getText());
    }


    @PostMapping("/activities")
    public ActivityResponseDTO suggestActivities(@RequestBody SuggestionsRequestDTO request) {
        return aiService.suggestActivities(request.getEmotion());
    }
}