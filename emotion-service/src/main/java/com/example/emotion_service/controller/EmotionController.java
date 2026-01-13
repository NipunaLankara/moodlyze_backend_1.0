package com.example.emotion_service.controller;

import com.example.emotion_service.service.EmotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/emotion")
@RequiredArgsConstructor
@CrossOrigin
public class EmotionController {

    @Autowired
    private EmotionService emotionService;

    @PostMapping("/image")
    public ResponseEntity<?> detectFromImage(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("X-User-Id") int userId
    ) {
        return ResponseEntity.ok(
                emotionService.detectFromImage(file, userId)
        );
    }

    @PostMapping("/speech")
    public ResponseEntity<?> detectFromSpeech(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("X-User-Id") int userId
    ) {
        return ResponseEntity.ok(
                emotionService.detectFromSpeech(file, userId)
        );
    }

    @GetMapping("/get-message-with-id")
    public String getMessageWithId(@RequestHeader("X-User-Id") int userId) {
         return "Emotion Service with id: " + userId;
    }

    @GetMapping("/get-message")
    public String getMessage() {
        return "Emotion Service";
    }
}

