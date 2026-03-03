package com.example.reminder_service.controller;

import com.example.reminder_service.dto.request.ReminderCreateRequestDTO;
import com.example.reminder_service.dto.request.ReminderUpdateRequestDTO;
import com.example.reminder_service.dto.response.ReminderResponseDTO;
import com.example.reminder_service.service.ReminderService;
import com.example.reminder_service.util.StandardResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reminders")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @PostMapping("/create")
    public StandardResponse createReminder(
            @RequestBody @Valid ReminderCreateRequestDTO dto,
            @RequestHeader("X-User-Id") int userId,
            @RequestHeader("X-Email") String email
    ) {

        String msg = reminderService.createReminder(dto, userId, email);

        return new StandardResponse(200, "Success", msg);
    }


    @GetMapping("/get-all")
    public StandardResponse getAllReminders(
            @RequestHeader("X-User-Id") int userId
    ) {

        List<ReminderResponseDTO> list =
                reminderService.getUserReminders(userId);

        return new StandardResponse(200, "Reminder List", list);
    }

    @GetMapping("/get/{id}")
    public StandardResponse getReminder(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") int userId
    ) {

        ReminderResponseDTO dto =
                reminderService.getReminderById(id, userId);

        return new StandardResponse(200, "Reminder Found", dto);
    }

    @PutMapping("/update/{id}")
    public StandardResponse updateReminder(
            @PathVariable Long id,
            @RequestBody ReminderUpdateRequestDTO dto,
            @RequestHeader("X-User-Id") int userId
    ) {

        ReminderResponseDTO updated =
                reminderService.updateReminder(id, userId, dto);

        return new StandardResponse(200, "Reminder Updated", updated);
    }

    @DeleteMapping("/delete/{id}")
    public StandardResponse deleteReminder(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") int userId
    ) {

        String msg = reminderService.deleteReminder(id, userId);

        return new StandardResponse(200, "Success", msg);
    }
}