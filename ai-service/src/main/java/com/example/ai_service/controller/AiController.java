package com.example.ai_service.controller;

import com.example.ai_service.dto.AiRequestDTO;
import com.example.ai_service.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/generate")
    public String generate(@RequestBody AiRequestDTO requestDTO) {
        return aiService.generateResponse(requestDTO.getPrompt());
    }
}