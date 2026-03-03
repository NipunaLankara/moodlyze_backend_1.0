package com.example.reminder_service.repo;

import com.example.reminder_service.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderRepo extends JpaRepository<Reminder, Long> {

    List<Reminder> findBySentFalseAndRemindAtBefore(LocalDateTime time);

    List<Reminder> findByUserId(int userId);

    Optional<Reminder> findByIdAndUserId(Long id, int userId);

}