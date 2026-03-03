package com.example.emotion_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class FastApiResponseDTO {
    private List<EmotionResultDTO> predictions;
}