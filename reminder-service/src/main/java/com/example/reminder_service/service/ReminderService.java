package com.example.reminder_service.service;

import com.example.reminder_service.dto.request.ReminderCreateRequestDTO;
import com.example.reminder_service.dto.request.ReminderUpdateRequestDTO;
import com.example.reminder_service.dto.response.ReminderResponseDTO;

import java.util.List;

public interface ReminderService {

    String createReminder(ReminderCreateRequestDTO dto, int userId, String email);

    List<ReminderResponseDTO> getUserReminders(int userId);

    ReminderResponseDTO getReminderById(Long id, int userId);

    ReminderResponseDTO updateReminder(Long id, int userId, ReminderUpdateRequestDTO dto);

    String deleteReminder(Long id, int userId);
}