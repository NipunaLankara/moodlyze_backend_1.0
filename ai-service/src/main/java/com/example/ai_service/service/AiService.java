package com.example.ai_service.service;

import com.example.ai_service.dto.EmotionResponseDTO;
import com.example.ai_service.dto.ActivityResponseDTO;

public interface AiService {
    EmotionResponseDTO detectEmotion(String text);
    ActivityResponseDTO suggestActivities(String emotion);
}