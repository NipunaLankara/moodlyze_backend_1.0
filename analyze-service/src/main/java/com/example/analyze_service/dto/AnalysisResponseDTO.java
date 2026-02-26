package com.example.analyze_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalysisResponseDTO {
    private String state;        // REST_REQUIRED or READY_TO_WORK
    private String detectedMood;
    private String message;      // System message
//    private String aiAdvice;     // Suggestions or Analysis
    private Object taskData;     // Null if mood is bad
}