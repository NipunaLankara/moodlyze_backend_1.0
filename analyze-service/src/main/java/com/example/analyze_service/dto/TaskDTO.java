package com.example.analyze_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskDTO {

    private Long id;
    private String title;
    private String priority; // HIGH, MEDIUM, LOW
    private LocalTime deadlineTime;
    private int estimatedTimeMinutes;
}