package com.example.analyze_service.service.impl;

import com.example.analyze_service.dto.AnalysisResponseDTO;
import com.example.analyze_service.entity.TaskAnalysis;
import com.example.analyze_service.repo.AnalysisRepo;
import com.example.analyze_service.service.AiServiceClient;
import com.example.analyze_service.service.AnalyzeService;
import com.example.analyze_service.service.EmotionClient;
import com.example.analyze_service.service.TaskClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnalyzeServiceIMPL implements AnalyzeService {

    @Autowired
    private TaskClient taskClient;
    @Autowired
    private EmotionClient emotionClient;
    @Autowired
    private AiServiceClient aiClient;
    @Autowired
    private AnalysisRepo analysisRepo;

    public AnalysisResponseDTO processUserStatus(int userId) {

        String currentMood = (String) emotionClient.getLatestEmotion(userId).getBody().getData();

        if (isBadMood(currentMood)) {
            // Bad Mood -Suggest Activities
            String suggestions = aiClient.generate("User is " + currentMood + ". Suggest 3 quick self-care activities.");
            return new AnalysisResponseDTO("REST_REQUIRED", currentMood, suggestions, null);
        } else {

//            return new AnalysisResponseDTO("READY_TO_WORK", currentMood, "this code pending....", null);

            Object pendingTasks = taskClient.getTasksByStatus("PENDING", userId).getBody().getData();

            // 2. Define Work Constraints
            String workStartTime = "08:00 AM";
            String workEndTime = "05:00 PM";

            // 3. Complex AI Prompt for Scheduling & Breakdown
            String prompt = "Act as a Smart Productivity Assistant. User Mood: " + currentMood + ".\n" +
                    "Work Hours: " + workStartTime + " to " + workEndTime + ".\n" +
                    "Tasks: " + pendingTasks.toString() + ".\n\n" +
                    "Instructions:\n" +
                    "1. Order tasks by Priority (HIGH first) and Deadline.\n" +
                    "2. If a task takes > 4 hours, break it into smaller sub-tasks (e.g., Part 1, Part 2).\n" +
                    "3. Assign specific time slots for each task starting from " + workStartTime + ".\n" +
                    "4. Insert 15-minute breaks after every 90 minutes of work.\n" +
                    "5. Output a clear, structured daily schedule.";

            String aiAnalysis = aiClient.generate(prompt);

            // 4. Save the Result
            TaskAnalysis analysis = new TaskAnalysis();
            analysis.setUserId(userId);
            analysis.setMoodAtTime(currentMood);
            analysis.setSmartPlan(aiAnalysis);
            analysis.setWorkingWindow(workStartTime + "-" + workEndTime);
            analysis.setCreatedAt(LocalDateTime.now());
            analysisRepo.save(analysis);

            return new AnalysisResponseDTO("READY_TO_WORK", currentMood, aiAnalysis, pendingTasks);
        }
    }

    private boolean isBadMood(String mood) {
        return List.of("SAD", "STRESSED", "ANGRY").contains(mood.toUpperCase());
    }
}