package com.example.analyze_service.service.impl;

import com.example.analyze_service.dto.AnalysisResponseDTO;
import com.example.analyze_service.service.AiServiceClient;
import com.example.analyze_service.service.AnalyzeService;
import com.example.analyze_service.service.EmotionClient;
import com.example.analyze_service.service.TaskClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyzeServiceIMPL implements AnalyzeService {

    @Autowired
    private TaskClient taskClient;
    @Autowired
    private EmotionClient emotionClient;
    @Autowired
    private AiServiceClient aiClient;

    public AnalysisResponseDTO processUserStatus(int userId) {

        String currentMood = (String) emotionClient.getLatestEmotion(userId).getBody().getData();

        if (isBadMood(currentMood)) {
            // Bad Mood -Suggest Activities
            String suggestions = aiClient.generate("User is " + currentMood + ". Suggest 3 quick self-care activities.");
            return new AnalysisResponseDTO("REST_REQUIRED", currentMood, suggestions, null);
        } else {

            return new AnalysisResponseDTO("READY_TO_WORK", currentMood, "this code pending....", null);

//            // Good Mood get ONLY PENDING tasks
//            Object pendingTasks = taskClient.getTasksByStatus("PENDING", userId).getBody().getData();
//
//            // AI summarize the taks
//            String prompt = "The user is feeling " + currentMood + ". " +
//                    "Here are their PENDING tasks: " + pendingTasks.toString() +
//                    ". Give a 1-sentence encouragement and tell them which task looks most important.";
//
//            String aiAnalysis = aiClient.generate(prompt);
//
//            return new AnalysisResponseDTO("READY_TO_WORK", currentMood, aiAnalysis, pendingTasks);
        }
    }

    private boolean isBadMood(String mood) {
        return List.of("SAD", "STRESSED", "ANGRY").contains(mood.toUpperCase());
    }
}