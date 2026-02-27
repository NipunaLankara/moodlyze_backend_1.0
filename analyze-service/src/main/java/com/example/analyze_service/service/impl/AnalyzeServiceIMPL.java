package com.example.analyze_service.service.impl;

import com.example.analyze_service.dto.AnalysisResponseDTO;
import com.example.analyze_service.dto.EmailRequestDTO;
import com.example.analyze_service.dto.ScheduleResponseDTO;
import com.example.analyze_service.dto.TaskDTO;
import com.example.analyze_service.entity.TaskAnalysis;
import com.example.analyze_service.entity.TaskSchedule;
import com.example.analyze_service.exception.NotFoundException;
import com.example.analyze_service.repo.AnalysisRepo;
import com.example.analyze_service.repo.TaskScheduleRepo;
import com.example.analyze_service.service.AnalyzeService;
import com.example.analyze_service.service.EmailApiClient;
import com.example.analyze_service.service.EmotionClient;
import com.example.analyze_service.service.TaskClient;
import com.example.analyze_service.util.StandardResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class AnalyzeServiceIMPL implements AnalyzeService {

    @Autowired
    private TaskClient taskClient;

    @Autowired
    private EmotionClient emotionClient;

    @Autowired
    private AnalysisRepo analysisRepo;

    @Autowired
    private TaskScheduleRepo scheduleRepo;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EmailApiClient emailApiClient;

    @Override
    public AnalysisResponseDTO processUserStatus(int userId, String email) {

        //  Get Current Mood
        String currentMood = (String) emotionClient
                .getLatestEmotion(userId)
                .getBody()
                .getData();

        if (isBadMood(currentMood)) {
            return new AnalysisResponseDTO(
                    "REST_REQUIRED",
                    currentMood,
                    "User requires recovery time. Suggest relaxation activities.",
                    null
            );
        }

        // Get Today's Pending Tasks
        StandardResponse response = taskClient
                .getTodayTasksByStatus("PENDING", userId)
                .getBody();

        List<TaskDTO> tasks = mapper.convertValue(
                response.getData(),
                new TypeReference<List<TaskDTO>>() {}
        );

        if (tasks.isEmpty()) {
            throw new NotFoundException("No pending tasks found for today.");
        }

        // Sort tasks by deadline → priority
        tasks.sort(
                Comparator
                        .comparing(TaskDTO::getDeadlineTime,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(TaskDTO::getPriority,
                                Comparator.nullsLast(String::compareTo))
        );

        // Save Analysis Header
        TaskAnalysis analysis = new TaskAnalysis();
        analysis.setUserId(userId);
        analysis.setMoodAtTime(currentMood);
        analysis.setCreatedAt(LocalDateTime.now());
        analysisRepo.save(analysis);

        //  Generate and Save Task Schedule
        generateAndSaveSchedule(tasks, analysis.getId());

        // Fetch saved schedule
        List<TaskSchedule> savedSchedules =
                scheduleRepo.findByAnalysisIdOrderByStartTimeAsc(analysis.getId());

        if (savedSchedules.isEmpty()) {
            throw new NotFoundException("Failed to generate schedule for today.");
        }

        //  Convert schedule to DTO
        List<ScheduleResponseDTO> scheduleResponse =
                savedSchedules.stream().map(s -> {
                    ScheduleResponseDTO dto = new ScheduleResponseDTO();
                    dto.setDisplayTitle(s.getDisplayTitle());
                    dto.setStartTime(s.getStartTime());
                    dto.setEndTime(s.getEndTime());
                    dto.setBreak(s.isBreak());
                    dto.setPartNumber(s.getPartNumber());
                    return dto;
                }).toList();
        StringBuilder scheduleText = new StringBuilder();

// Show date once at the top
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        scheduleText.append("Hello,\n\n");
        scheduleText.append("Here is your task schedule for ")
                .append(today.format(dateFormatter))
                .append(":\n\n");

        for (ScheduleResponseDTO s : scheduleResponse) {
            if (s.isBreak()) {
                scheduleText.append(String.format("Break: %s - %s\n",
                        s.getStartTime().format(timeFormatter),
                        s.getEndTime().format(timeFormatter)));
            } else {
                scheduleText.append(String.format("%s: %s - %s%s\n",
                        s.getDisplayTitle(),
                        s.getStartTime().format(timeFormatter),
                        s.getEndTime().format(timeFormatter),
                        s.getPartNumber() != null ? " (Part " + s.getPartNumber() + ")" : ""));
            }
        }

        scheduleText.append("\nHave a productive day!");

// Send Email
        EmailRequestDTO emailRequest = new EmailRequestDTO();
        emailRequest.setTo(email);
        emailRequest.setSubject("Your Daily Task Schedule");
        emailRequest.setBody(scheduleText.toString());
        emailApiClient.sendEmail(emailRequest);

        // Return Response
        return new AnalysisResponseDTO(
                "READY_TO_WORK",
                currentMood,
                "Structured schedule generated successfully.",
                scheduleResponse
        );
    }

    /**
     * Generate and save schedule with:
     * - Max 90 minutes per task part
     * - 10-minute break after each part
     */
    private void generateAndSaveSchedule(List<TaskDTO> tasks, Long analysisId) {

        LocalDateTime currentTime = LocalDateTime.now();

        for (TaskDTO task : tasks) {
            int remaining = task.getEstimatedTimeMinutes();
            int part = 1;

            while (remaining > 0) {
                int session = Math.min(remaining, 90);
                LocalDateTime endTime = currentTime.plusMinutes(session);

                // Save Task Part
                TaskSchedule schedule = new TaskSchedule();
                schedule.setAnalysisId(analysisId);
                schedule.setTaskId(task.getId());
                schedule.setTitle(task.getTitle());
                if (task.getEstimatedTimeMinutes() > 90) {
                    schedule.setPartNumber(part);
                    schedule.setDisplayTitle(task.getTitle() + " (Part " + part + ")");
                } else {
                    schedule.setDisplayTitle(task.getTitle());
                }
                schedule.setStartTime(currentTime);
                schedule.setEndTime(endTime);
                schedule.setBreak(false);
                scheduleRepo.save(schedule);

                currentTime = endTime;
                remaining -= session;
                part++;

                // Add 10-minute break
                TaskSchedule breakSchedule = new TaskSchedule();
                breakSchedule.setAnalysisId(analysisId);
                breakSchedule.setTitle("Short Break");
                breakSchedule.setDisplayTitle("Short Break");
                breakSchedule.setStartTime(currentTime);
                breakSchedule.setEndTime(currentTime.plusMinutes(10));
                breakSchedule.setBreak(true);
                scheduleRepo.save(breakSchedule);

                currentTime = currentTime.plusMinutes(10);
            }
        }
    }

    private boolean isBadMood(String mood) {
        return List.of("SAD", "STRESSED", "ANGRY").contains(mood.toUpperCase());
    }
}