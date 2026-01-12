package com.example.emotion_service.dto;

import lombok.Data;

@Data
public class EmotionResultDTO {
    private String emotion;
    private double confidence;
}
