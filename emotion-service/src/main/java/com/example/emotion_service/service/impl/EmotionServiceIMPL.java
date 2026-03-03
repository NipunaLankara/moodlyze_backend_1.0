package com.example.emotion_service.service.impl;

import com.example.emotion_service.dto.EmotionResponseDTO;
import com.example.emotion_service.dto.EmotionResultDTO;
import com.example.emotion_service.dto.FastApiResponseDTO;
import com.example.emotion_service.dto.TextEmotionRequestDTO;
import com.example.emotion_service.entity.EmotionRecord;
import com.example.emotion_service.exception.EmotionDetectionException;
import com.example.emotion_service.repo.EmotionRepo;
import com.example.emotion_service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public class EmotionServiceIMPL implements EmotionService {
    @Autowired
    private EmotionRepo emotionRepo;
    @Autowired
    private EmotionImageMlClient emotionImageMlClient;
    @Autowired
    private EmotionSpeechMlClient emotionSpeechMlClient;
    @Autowired
    private AiServiceClient aiServiceClient;

    @Override
    public Object detectFromImage(MultipartFile file, int userId) {
        try {
//            FastApiResponseDTO response = emotionMlClient.detectImageEmotion(file);
            FastApiResponseDTO response = emotionImageMlClient.detectImageEmotion(file);

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
//            EmotionResultDTO result = emotionMlClient.detectSpeechEmotion(file);
              EmotionResultDTO result = emotionSpeechMlClient.detectSpeechEmotion(file);

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
            System.out.println("Sending text to AI service: " + textEmotionRequestDTO.getText());

            EmotionResponseDTO response = aiServiceClient.getAiResponse(textEmotionRequestDTO);

            if (response == null || response.getEmotion() == null) {
                throw new EmotionDetectionException("AI service returned null emotion");
            }

            String result = response.getEmotion().trim();
            System.out.println("AI emotion result: " + result);

            emotionRepo.save(new EmotionRecord(
                    null, userId, result.toUpperCase(), "TEXT", LocalDateTime.now()
            ));

            return result;

        } catch (feign.FeignException e) {
            // 🔥 log real Feign error
            System.err.println("Feign error status: " + e.status());
            System.err.println("Feign error body: " + e.contentUTF8());
            e.printStackTrace();

            throw new EmotionDetectionException(
                    "AI Service error: " + e.contentUTF8()
            );

        } catch (Exception e) {
            e.printStackTrace();
            throw new EmotionDetectionException(
                    "Failed to process text emotion: " + e.getMessage()
            );
        }
    }

    @Override
    public String getLatestEmotion(int userId) {

        return emotionRepo.findTopByUserIdOrderByDetectedAtDesc(userId)
                .map(EmotionRecord::getEmotion) // Extracts only the "HAPPY"/"SAD" string
                .orElseThrow(() -> new EmotionDetectionException("No emotion records found for user ID: " + userId));
    }
}
