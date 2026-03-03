package com.example.analyze_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScheduleResponseDTO {

    private Long id;
    private String displayTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isBreak;
    private Integer partNumber;
    private Long taskId;
}