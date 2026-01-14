package com.example.emotion_service.controller;


import com.example.emotion_service.dto.TextEmotionRequestDTO;
import com.example.emotion_service.service.EmotionService;
import com.example.emotion_service.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/emotion")
@RequiredArgsConstructor
@CrossOrigin
public class EmotionController {

    private final EmotionService emotionService;

    @PostMapping("/image")
    public ResponseEntity<StandardResponse> detectFromImage(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("X-User-Id") int userId
    ) {
        Object result = emotionService.detectFromImage(file, userId);
        return new ResponseEntity<>(
                new StandardResponse(200, "Image Emotion Detected Successfully", result),
                HttpStatus.OK
        );
    }

    @PostMapping("/speech")
    public ResponseEntity<StandardResponse> detectFromSpeech(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("X-User-Id") int userId
    ) {
        Object result = emotionService.detectFromSpeech(file, userId);
        return new ResponseEntity<>(
                new StandardResponse(200, "Speech Emotion Detected Successfully", result),
                HttpStatus.OK
        );
    }

    @PostMapping("/text")
    public ResponseEntity<StandardResponse> detectFromText(
            @RequestBody TextEmotionRequestDTO textEmotionRequestDTO,
            @RequestHeader("X-User-Id") int userId

    ){
        String emotion = emotionService.detectFromText(textEmotionRequestDTO,userId);

        return new ResponseEntity<>(
                new StandardResponse(200, "Text Emotion Detected Successfully", emotion),
                HttpStatus.OK
        );

    }

    @GetMapping("/get-message-with-id")
    public ResponseEntity<StandardResponse> getMessageWithId(@RequestHeader("X-User-Id") int userId) {
        String msg = "Emotion Service with id: " + userId;
        return new ResponseEntity<>(
                new StandardResponse(200, "Success", msg),
                HttpStatus.OK
        );
    }

    @GetMapping("/get-message")
    public ResponseEntity<StandardResponse> getMessage() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Success", "Emotion Service is Active"),
                HttpStatus.OK
        );
    }
}