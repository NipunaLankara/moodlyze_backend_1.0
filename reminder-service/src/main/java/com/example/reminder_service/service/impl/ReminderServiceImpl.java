package com.example.reminder_service.service.impl;

import com.example.reminder_service.dto.request.ReminderCreateRequestDTO;
import com.example.reminder_service.dto.request.ReminderUpdateRequestDTO;
import com.example.reminder_service.dto.response.ReminderResponseDTO;
import com.example.reminder_service.entity.Reminder;
import com.example.reminder_service.exception.NotFoundException;
import com.example.reminder_service.mapper.ReminderMapper;
import com.example.reminder_service.repo.ReminderRepo;
import com.example.reminder_service.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderServiceImpl implements ReminderService {

    @Autowired
    private ReminderRepo reminderRepo;

    @Autowired
    private ReminderMapper reminderMapper;

    @Override
    public String createReminder(ReminderCreateRequestDTO dto, int userId, String email) {

        Reminder reminder = new Reminder();

        reminder.setUserId(userId);
        reminder.setEmail(email);
        reminder.setMessage(dto.getMessage());
        reminder.setRemindAt(dto.getRemindAt());

        reminderRepo.save(reminder);

        return "Reminder created successfully";
    }

    @Override
    public List<ReminderResponseDTO> getUserReminders(int userId) {

        List<Reminder> reminders = reminderRepo.findByUserId(userId);

        return reminders.stream()
                .map(reminderMapper::entityToDto)
                .toList();
    }

    @Override
    public ReminderResponseDTO getReminderById(Long id, int userId) {

        Reminder reminder = reminderRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Reminder not found"));

        return reminderMapper.entityToDto(reminder);
    }

    @Override
    public ReminderResponseDTO updateReminder(Long id, int userId, ReminderUpdateRequestDTO dto) {

        Reminder reminder = reminderRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Reminder not found"));

        if (dto.getMessage() != null) {
            reminder.setMessage(dto.getMessage());
        }

        if (dto.getRemindAt() != null) {
            reminder.setRemindAt(dto.getRemindAt());
        }

        Reminder updated = reminderRepo.save(reminder);

        return reminderMapper.entityToDto(updated);
    }

    @Override
    public String deleteReminder(Long id, int userId) {

        Reminder reminder = reminderRepo.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Reminder not found"));

        reminderRepo.delete(reminder);

        return "Reminder deleted successfully";
    }
}