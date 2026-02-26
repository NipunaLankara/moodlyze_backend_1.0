package com.example.emotion_service.service;

import com.example.emotion_service.config.FeignMultipartConfig;
import com.example.emotion_service.dto.EmotionResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
        name = "emotion-speech-ml-service",
        url = "http://127.0.0.1:8001",
        configuration = FeignMultipartConfig.class
)
public interface EmotionSpeechMlClient {

    @PostMapping(
            value = "/chat/speech-emotion/predict/uploadfile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    EmotionResultDTO detectSpeechEmotion(@RequestPart("file") MultipartFile file);
}