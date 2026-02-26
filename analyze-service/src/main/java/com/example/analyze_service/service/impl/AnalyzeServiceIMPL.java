package com.example.analyze_service.service.impl;

import com.example.analyze_service.dto.AnalysisResponseDTO;
import com.example.analyze_service.dto.TaskDTO;
import com.example.analyze_service.entity.TaskAnalysis;
import com.example.analyze_service.entity.TaskSchedule;
import com.example.analyze_service.repo.AnalysisRepo;
import com.example.analyze_service.repo.TaskScheduleRepo;
import com.example.analyze_service.service.AnalyzeService;
import com.example.analyze_service.service.EmotionClient;
import com.example.analyze_service.service.TaskClient;
import com.example.analyze_service.util.StandardResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

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

    @Override
    public AnalysisResponseDTO processUserStatus(int userId) {

        // Get Mood
        String currentMood = (String) emotionClient
                .getLatestEmotion(userId)
                .getBody()
                .getData();

        // If Bad Mood → REST_REQUIRED
        if (isBadMood(currentMood)) {
            return new AnalysisResponseDTO(
                    "REST_REQUIRED",
                    currentMood,
                    "User requires recovery time. Suggest relaxation activities.",
                    null
            );
        }

        // Get Pending Tasks
        StandardResponse response = taskClient
                .getTasksByStatus("PENDING", userId)
                .getBody();

        List<TaskDTO> tasks = mapper.convertValue(
                response.getData(),
                new TypeReference<List<TaskDTO>>() {}
        );

        if (tasks.isEmpty()) {
            return new AnalysisResponseDTO(
                    "READY_TO_WORK",
                    currentMood,
                    "No pending tasks available.",
                    tasks
            );
        }

        // Sort by Deadline → Priority
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
        analysis.setWorkingWindow("08:00-17:00");
        analysis.setCreatedAt(LocalDateTime.now());
        analysisRepo.save(analysis);

        // Generate Structured Schedule
        generateAndSaveSchedule(tasks, analysis.getId());

        return new AnalysisResponseDTO(
                "READY_TO_WORK",
                currentMood,
                "Structured schedule generated successfully.",
                tasks
        );
    }

    private void generateAndSaveSchedule(List<TaskDTO> tasks, Long analysisId) {

        LocalDate today = LocalDate.now();
        LocalDateTime currentTime = LocalDateTime.of(today, LocalTime.of(8, 0));
        LocalDateTime workEnd = LocalDateTime.of(today, LocalTime.of(17, 0));

        int workCounter = 0;

        for (TaskDTO task : tasks) {

            int remaining = task.getEstimatedTimeMinutes();
            int part = 1;

            while (remaining > 0 && currentTime.isBefore(workEnd)) {

                int session = Math.min(remaining, 240);
                LocalDateTime endTime = currentTime.plusMinutes(session);

                if (endTime.isAfter(workEnd)) break;

                TaskSchedule schedule = new TaskSchedule();
                schedule.setAnalysisId(analysisId);
                schedule.setTaskId(task.getId());
                schedule.setTitle(task.getTitle());

                if (task.getEstimatedTimeMinutes() > 240) {
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
                workCounter += session;

                // Add break every 90 mins
                if (workCounter >= 90) {

                    LocalDateTime breakEnd = currentTime.plusMinutes(15);

                    if (breakEnd.isBefore(workEnd)) {

                        TaskSchedule breakSchedule = new TaskSchedule();
                        breakSchedule.setAnalysisId(analysisId);
                        breakSchedule.setTitle("Mental Recharge Break");
                        breakSchedule.setDisplayTitle("Mental Recharge Break");
                        breakSchedule.setStartTime(currentTime);
                        breakSchedule.setEndTime(breakEnd);
                        breakSchedule.setBreak(true);

                        scheduleRepo.save(breakSchedule);

                        currentTime = breakEnd;
                    }

                    workCounter = 0;
                }
            }
        }
    }

    private boolean isBadMood(String mood) {
        return List.of("SAD", "STRESSED", "ANGRY")
                .contains(mood.toUpperCase());
    }

    private int getPriorityWeight(String priority) {
        return switch (priority.toUpperCase()) {
            case "HIGH" -> 1;
            case "MEDIUM" -> 2;
            case "LOW" -> 3;
            default -> 4;
        };
    }
}