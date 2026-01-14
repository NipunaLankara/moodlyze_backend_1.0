package com.example.emotion_service.service.impl;

import com.example.emotion_service.dto.EmotionResultDTO;
import com.example.emotion_service.dto.FastApiResponseDTO;
import com.example.emotion_service.dto.TextEmotionRequestDTO;
import com.example.emotion_service.entity.EmotionRecord;
import com.example.emotion_service.exception.EmotionDetectionException;
import com.example.emotion_service.repo.EmotionRepo;
import com.example.emotion_service.service.AiServiceClient;
import com.example.emotion_service.service.EmotionMlClient;
import com.example.emotion_service.service.EmotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class EmotionServiceIMPL implements EmotionService {
    @Autowired
    private EmotionRepo emotionRepo;
    @Autowired
    private EmotionMlClient emotionMlClient;
    @Autowired
    private AiServiceClient aiServiceClient;

    @Override
    public Object detectFromImage(MultipartFile file, int userId) {
        try {
            FastApiResponseDTO response = emotionMlClient.detectImageEmotion(file);

            if (response == null || response.getPredictions() == null || response.getPredictions().isEmpty()) {
                throw new EmotionDetectionException("No emotion detected in the provided image.");
            }

            EmotionResultDTO topResult = response.getPredictions().get(0);

            emotionRepo.save(new EmotionRecord(
                    null, userId, topResult.getEmotion(), "IMAGE", LocalDateTime.now()
            ));

            return topResult;
        } catch (Exception e) {
            throw new EmotionDetectionException("Failed to process image: " + e.getMessage());
        }
    }

    @Override
    public Object detectFromSpeech(MultipartFile file, int userId) {
        try {
            EmotionResultDTO result = emotionMlClient.detectSpeechEmotion(file);

            if (result == null || result.getEmotion() == null) {
                throw new EmotionDetectionException("Could not analyze speech emotion.");
            }

            emotionRepo.save(new EmotionRecord(
                    null, userId, result.getEmotion(), "SPEECH", LocalDateTime.now()
            ));

            return result;
        } catch (Exception e) {
            throw new EmotionDetectionException("Failed to process speech: " + e.getMessage());
        }
    }

    @Override
    public String detectFromText(TextEmotionRequestDTO textEmotionRequestDTO, int userId) {
        try {
//            return  textEmotionRequestDTO.getPrompt();

            String prompt = "Analyze the emotion of the following text and respond with ONLY one word " +
                    "(e.g., HAPPY, SAD, ANGRY, NEUTRAL, SURPRISED): " + textEmotionRequestDTO.getPrompt();

            String result = aiServiceClient.getAiResponse(prompt);

            if (result == null || result.trim().isEmpty()) {
                throw new EmotionDetectionException("AI service returned an empty response.");
            }
            emotionRepo.save(new EmotionRecord(
                    null, userId, result.toUpperCase().trim(), "TEXT", LocalDateTime.now()
            ));

            return result;

        } catch (feign.FeignException e) {
            // catch Feign issues
            throw new EmotionDetectionException("AI Service is currently unavailable or unreachable.");
        } catch (Exception e) {
            // Catch  other unexpected errors
            throw new EmotionDetectionException("Failed to process text emotion: " + e.getMessage());
        }
    }

    @Override
    public String getLatestEmotion(int userId) {

        return emotionRepo.findTopByUserIdOrderByDetectedAtDesc(userId)
                .map(EmotionRecord::getEmotion) // Extracts only the "HAPPY"/"SAD" string
                .orElseThrow(() -> new EmotionDetectionException("No emotion records found for user ID: " + userId));
    }
}
