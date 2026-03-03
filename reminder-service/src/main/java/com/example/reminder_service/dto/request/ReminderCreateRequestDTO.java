package com.example.reminder_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReminderCreateRequestDTO {

    @NotBlank
    private String message;

    @NotNull
    private LocalDateTime remindAt;
}