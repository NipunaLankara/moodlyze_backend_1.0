package com.example.ai_service.service.impl;

import com.example.ai_service.dto.EmotionResponseDTO;
import com.example.ai_service.dto.ActivityResponseDTO;
import com.example.ai_service.service.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class AiServiceIMPL  implements AiService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String groqUrl;

    public AiServiceIMPL(@Value("${ai.groq.api-key}") String apiKey,
                         @Value("${ai.groq.url}") String groqUrl) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
        this.groqUrl = groqUrl;
    }

    public EmotionResponseDTO detectEmotion(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String emotionPrompt =
                "Analyze the sentence and return ONLY ONE emotion word (happy, sad, angry, anxious, stressed, calm). No explanation. Sentence: "
                        + prompt;

        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "user", "content", emotionPrompt)
                ),
                "temperature", 0
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        String response = restTemplate.postForObject(groqUrl, request, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            String emotion = root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText()
                    .trim()
                    .toLowerCase();

            return new EmotionResponseDTO(emotion);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse emotion response", e);
        }
    }

    public ActivityResponseDTO suggestActivities(String emotion) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String activityPrompt =
                "Suggest 10 short and practical activities for someone who feels " + emotion +
                        ". Return as a comma separated list only.";

        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "user", "content", activityPrompt)
                ),
                "temperature", 0.5
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        String response = restTemplate.postForObject(groqUrl, request, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            String activitiesText = root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText()
                    .trim();

            List<String> activities = List.of(activitiesText.split(","));

            return new ActivityResponseDTO(activities);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse activities response", e);
        }
    }
}