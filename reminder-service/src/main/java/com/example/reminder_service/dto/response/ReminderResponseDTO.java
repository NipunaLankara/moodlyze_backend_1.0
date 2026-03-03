package com.example.reminder_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReminderResponseDTO {

    private Long id;
    private String message;
    private LocalDateTime remindAt;
    private boolean sent;
}