package com.example.task_service.dto.request;

import com.example.task_service.entity.enums.Priority;
import com.example.task_service.entity.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskUpdateRequestDTO {
    private String title;
    private String description;
    private Priority priority;
    private Integer estimatedTimeMinutes;
    private LocalTime deadlineTime;
    private LocalDate taskDate;
    private TaskStatus status;
}
