package com.example.task_service.dto.request;

import com.example.task_service.entity.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskCreateRequestDTO {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Priority priority;

    @NotNull
    private Integer estimatedTimeMinutes;

    private LocalTime deadlineTime;

    @NotNull
    private LocalDate taskDate;
}
