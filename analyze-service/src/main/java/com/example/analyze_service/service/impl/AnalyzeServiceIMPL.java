package com.example.analyze_service.service.impl;

import com.example.analyze_service.dto.*;
import com.example.analyze_service.entity.TaskAnalysis;
import com.example.analyze_service.entity.TaskSchedule;
import com.example.analyze_service.exception.NotFoundException;
import com.example.analyze_service.repo.AnalysisRepo;
import com.example.analyze_service.repo.TaskScheduleRepo;
import com.example.analyze_service.service.*;
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

    @Autowired
    private AiServiceClient aiServiceClient;

    @Override
    public AnalysisResponseDTO processUserStatus(int userId, String email) {


        String currentMood = "NEUTRAL"; // default mood

        try {
            StandardResponse emotionResponse = emotionClient
                    .getLatestEmotion(userId)
                    .getBody();

            if (emotionResponse != null && emotionResponse.getData() != null) {
                currentMood = emotionResponse.getData().toString();
            }

        } catch (Exception e) {
            System.out.println("Emotion service unavailable or no data found. Using default mood: NEUTRAL");
        }

        if (isBadMood(currentMood)) {

            ActivityResponseDTO activityResponseDTO =
                    aiServiceClient.getActivities(new SuggestionsRequestDTO(currentMood));

            EmailRequestDTO emailRequest = new EmailRequestDTO();
            emailRequest.setTo(email);
            emailRequest.setSubject("Your Daily Task Schedule");
            emailRequest.setBody(activityResponseDTO.getActivities().toString());
            emailApiClient.sendEmail(emailRequest);

            return new AnalysisResponseDTO(
                    "REST_REQUIRED",
                    currentMood,
                    "You seem "+ currentMood+ ". Try these calming activities.",
                    null,
                    activityResponseDTO.getActivities()
            );
        }
        // Get Today's Pending Tasks
        StandardResponse response;
        try {
            response = taskClient
                    .getTodayTasksByStatus("PENDING", userId)
                    .getBody();
        } catch (feign.FeignException.NotFound e) {
            // Task-service returned 404
            throw new NotFoundException("No pending tasks found for today.");

        } catch (feign.FeignException e) {
            // Other feign errors (500, 400 etc.)
            throw new RuntimeException("Task service unavailable. Please try again later.");
        }

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
                scheduleRepo.findByAnalysisIdAndStatusOrderByStartTimeAsc(
                        analysis.getId(),
                        "PENDING"
                );

        if (savedSchedules.isEmpty()) {
            throw new NotFoundException("Failed to generate schedule for today.");
        }


        List<ScheduleResponseDTO> scheduleResponse =
                savedSchedules.stream().map(s -> {
                    ScheduleResponseDTO dto = new ScheduleResponseDTO();
                    dto.setId(s.getId());
                    dto.setDisplayTitle(s.getDisplayTitle());
                    dto.setStartTime(s.getStartTime());
                    dto.setEndTime(s.getEndTime());
                    dto.setBreak(s.isBreak());
                    dto.setPartNumber(s.getPartNumber());
                    dto.setTaskId(s.getTaskId());
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


        return new AnalysisResponseDTO(
                "READY_TO_WORK",
                currentMood,
                "Structured schedule generated successfully.",
                scheduleResponse,
                null
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
                schedule.setStatus("PENDING");
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
                breakSchedule.setStatus("PENDING");
                scheduleRepo.save(breakSchedule);

                currentTime = currentTime.plusMinutes(10);
            }
        }
    }

    private boolean isBadMood(String mood) {
        if (mood == null) return false;

        return List.of(
                "SAD","STRESSED","ANXIOUS","ANGRY","DEPRESSED",
                "FRUSTRATED","OVERWHELMED","TIRED","LONELY",
                "IRRITATED","WORRIED","DISCOURAGED","HOPELESS","BURNT_OUT","FEAR"
        ).contains(mood.toUpperCase());
    }

    @Override
    public void completeSchedulePart(Long scheduleId) {

        TaskSchedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule part not found"));

        if (schedule.isBreak()) {
            schedule.setStatus("COMPLETED");
            scheduleRepo.save(schedule);
            return;
        }

        //  Mark this schedule part completed
        schedule.setStatus("COMPLETED");
        scheduleRepo.save(schedule);

        Long taskId = schedule.getTaskId();

        if (taskId != null) {
            // Check if any PENDING parts remain for this task
            List<TaskSchedule> remainingParts =
                    scheduleRepo.findByTaskIdAndStatus(taskId, "PENDING");

            boolean hasPendingParts = remainingParts.stream()
                    .anyMatch(s -> !s.isBreak());

            // If no pending parts → mark task COMPLETED in task-service
            if (!hasPendingParts) {
                try {
                    taskClient.markTaskCompleted(taskId);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to update task status in task-service");
                }
            }
        }
    }

    @Override
    public AnalysisResponseDTO getTodaySchedule(int userId) {
        LocalDate today = LocalDate.now();
        TaskAnalysis analysis = analysisRepo.findTopByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                userId,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        ).orElseThrow(() -> new NotFoundException("No schedule found for today"));

        List<TaskSchedule> savedSchedules = scheduleRepo.findByAnalysisIdAndStatusOrderByStartTimeAsc(
                analysis.getId(),
                "PENDING"
        );

        List<ScheduleResponseDTO> scheduleResponse = savedSchedules.stream().map(s -> {
            ScheduleResponseDTO dto = new ScheduleResponseDTO();
            dto.setId(s.getId());
            dto.setDisplayTitle(s.getDisplayTitle());
            dto.setStartTime(s.getStartTime());
            dto.setEndTime(s.getEndTime());
            dto.setBreak(s.isBreak());
            dto.setPartNumber(s.getPartNumber());
            dto.setTaskId(s.getTaskId());
            return dto;
        }).toList();

        return new AnalysisResponseDTO(
                "READY_TO_WORK",
                analysis.getMoodAtTime(),
                "Structured schedule generated successfully.",
                scheduleResponse,
                null // no activities
        );
    }
}