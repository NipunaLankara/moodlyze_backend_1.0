package com.example.emotion_service.service;

import com.example.emotion_service.dto.TextEmotionRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface EmotionService {
    Object detectFromImage(MultipartFile file, int userId);

    Object detectFromSpeech(MultipartFile file, int userId);

    String detectFromText(TextEmotionRequestDTO textEmotionRequestDTO, int userId);

    String getLatestEmotion(int userId);
}
