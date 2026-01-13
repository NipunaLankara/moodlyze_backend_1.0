package com.example.emotion_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface EmotionService {
    Object detectFromImage(MultipartFile file, int userId);

    Object detectFromSpeech(MultipartFile file, int userId);
}
