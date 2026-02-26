//package com.example.analyze_service.service.impl;
//
//import com.example.analyze_service.dto.AnalysisResponseDTO;
//import com.example.analyze_service.entity.TaskAnalysis;
//import com.example.analyze_service.repo.AnalysisRepo;
//import com.example.analyze_service.service.AiServiceClient;
//import com.example.analyze_service.service.AnalyzeService;
//import com.example.analyze_service.service.EmotionClient;
//import com.example.analyze_service.service.TaskClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class AnalyzeServiceIMPL implements AnalyzeService {
//
//    @Autowired
//    private TaskClient taskClient;
//    @Autowired
//    private EmotionClient emotionClient;
//    @Autowired
//    private AiServiceClient aiClient;
//    @Autowired
//    private AnalysisRepo analysisRepo;
//
//    public AnalysisResponseDTO processUserStatus(int userId) {
//
//        String currentMood = (String) emotionClient.getLatestEmotion(userId).getBody().getData();
//
//        if (isBadMood(currentMood)) {
//            // Bad Mood -Suggest Activities
//            String suggestions = aiClient.generate("User is " + currentMood + ". Suggest 3 quick self-care activities.");
//            return new AnalysisResponseDTO("REST_REQUIRED", currentMood, suggestions, null);
//        } else {
//
////            return new AnalysisResponseDTO("READY_TO_WORK", currentMood, "this code pending....", null);
//
//            Object pendingTasks = taskClient.getTasksByStatus("PENDING", userId).getBody().getData();
//
//            System.out.println(pendingTasks);
//
//
//            // Define Work Constraints
//            String workStartTime = "08:00 AM";
//            String workEndTime = "05:00 PM";
//
//            String prompt = "Act as an Expert Productivity Coach and Scheduler.\n" +
//                    "CONTEXT:\n" +
//                    "- User Mood: " + currentMood + " (The tone of your response should match this mood).\n" +
//                    "- Working Hours: " + workStartTime + " to " + workEndTime + ".\n" +
//                    "- Task List: " + pendingTasks.toString() + ".\n\n" +
//
//                    "SCHEDULING LOGIC:\n" +
//                    "1. DEADLINE FIRST: Tasks with an approaching 'deadlineTime' must be scheduled first.\n" +
//                    "2. PRIORITY SECOND: Among tasks with similar deadlines, prioritize HIGH over MEDIUM and LOW.\n" +
//                    "3. DURATION LOGIC: Use 'estimatedTimeMinutes' to fill the schedule.\n" +
//                    "4. TASK BREAKDOWN: If any single task is > 240 minutes (4 hours), you MUST break it into 'Part 1', 'Part 2', etc., and space them out.\n" +
//                    "5. BUFFER & BREAKS: Add a 15-minute 'Mental Recharge' break every 90 minutes of work.\n\n" +
//
//                    "OUTPUT FORMAT:\n" +
//                    "- Start with a brief, encouraging message based on the mood: " + currentMood + ".\n" +
//                    "- Provide a Timeline (e.g., 08:00 AM - 09:30 AM: [Task Title]).\n" +
//                    "- Explicitly state if a task was broken down because it was too long.\n" +
//                    "- End with one 'Pro-Tip' for staying focused today.";
//
//            String aiAnalysis = aiClient.generate(prompt);
//
//            TaskAnalysis analysis = new TaskAnalysis();
//            analysis.setUserId(userId);
//            analysis.setMoodAtTime(currentMood);
//            analysis.setSmartPlan(aiAnalysis);
//            analysis.setWorkingWindow(workStartTime + "-" + workEndTime);
//            analysis.setCreatedAt(LocalDateTime.now());
//            analysisRepo.save(analysis);
//
//            return new AnalysisResponseDTO("READY_TO_WORK", currentMood, aiAnalysis, pendingTasks);
//        }
//    }
//
//    private boolean isBadMood(String mood) {
//        return List.of("SAD", "STRESSED", "ANGRY").contains(mood.toUpperCase());
//    }
//}