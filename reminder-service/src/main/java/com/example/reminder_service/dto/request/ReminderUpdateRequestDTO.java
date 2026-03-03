package com.example.reminder_service.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReminderUpdateRequestDTO {

    private String message;

    private LocalDateTime remindAt;
}