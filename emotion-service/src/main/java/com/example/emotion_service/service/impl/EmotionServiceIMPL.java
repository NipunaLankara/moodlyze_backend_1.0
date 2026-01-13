package com.example.emotion_service.service.impl;

import com.example.emotion_service.dto.EmotionResultDTO;
import com.example.emotion_service.dto.FastApiResponseDTO;
import com.example.emotion_service.entity.EmotionRecord;
import com.example.emotion_service.repo.EmotionRepo;
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


    @Override
    public Object detectFromImage(MultipartFile file, int userId) {
        FastApiResponseDTO response = emotionMlClient.detectImageEmotion(file);

//        get top prediction (index 0)
        if (response != null && response.getPredictions() != null && !response.getPredictions().isEmpty()) {
            EmotionResultDTO topResult = response.getPredictions().get(0);


            emotionRepo.save(
                    new EmotionRecord(
                            null,
                            userId,
                            topResult.getEmotion(),
                            "IMAGE",
                            LocalDateTime.now()
                    )
            );
            return topResult; // Return the top result
        }

        return null;
    }

    @Override
    public Object detectFromSpeech(MultipartFile file, int userId) {
        EmotionResultDTO result = emotionMlClient.detectSpeechEmotion(file);

        emotionRepo.save(
                new EmotionRecord(
                        null,
                        userId,
                        result.getEmotion(),
                        "SPEECH",
                        LocalDateTime.now()
                )
        );
        return result;
    }
}
