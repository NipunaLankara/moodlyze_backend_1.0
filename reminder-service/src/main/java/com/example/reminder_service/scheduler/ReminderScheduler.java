package com.example.reminder_service.scheduler;

import com.example.reminder_service.client.EmailApiClient;

import com.example.reminder_service.dto.request.EmailRequestDTO;
import com.example.reminder_service.entity.Reminder;
import com.example.reminder_service.repo.ReminderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReminderScheduler {

    @Autowired
    private ReminderRepo reminderRepo;
    @Autowired
    private EmailApiClient emailApiClient;

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void processReminders() {

        List<Reminder> reminders =
                reminderRepo.findBySentFalseAndRemindAtBefore(LocalDateTime.now());

        for (Reminder reminder : reminders) {

            EmailRequestDTO email = new EmailRequestDTO(
                    reminder.getEmail(),
                    "Task Reminder",
                    reminder.getMessage()
            );

            emailApiClient.sendEmail(email);

            reminder.setSent(true);
            reminderRepo.save(reminder);
        }
    }
}