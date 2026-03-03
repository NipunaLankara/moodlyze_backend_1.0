package com.example.reminder_service.mapper;

import com.example.reminder_service.dto.response.ReminderResponseDTO;
import com.example.reminder_service.entity.Reminder;
import org.springframework.stereotype.Component;

@Component
public class ReminderMapper {

    public ReminderResponseDTO entityToDto(Reminder reminder) {

        ReminderResponseDTO dto = new ReminderResponseDTO();

        dto.setId(reminder.getId());
        dto.setMessage(reminder.getMessage());
        dto.setRemindAt(reminder.getRemindAt());
        dto.setSent(reminder.isSent());

        return dto;
    }
}