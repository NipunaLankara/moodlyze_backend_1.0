package com.example.analyze_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisResponseDTO {

    private String state;          // REST_REQUIRED or READY_TO_WORK
    private String detectedMood;
    private String message;

    // only for READY_TO_WORK
    private List<ScheduleResponseDTO> schedule;

    // only for REST_REQUIRED
    private List<String> activities;
}