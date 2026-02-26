package com.example.ai_service.service.impl;

import com.example.ai_service.service.AiService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class AiServiceIMPL implements AiService {

    private final ChatModel chatModel;

    public AiServiceIMPL(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String generateResponse(String userPrompt) {
        try {

            // Option A: Use default settings from application.properties
            // ChatResponse response = chatModel.call(new Prompt(userPrompt));

            GoogleGenAiChatOptions options = GoogleGenAiChatOptions.builder()
                    .model("gemini-2.0-flash") // Updated from 1.5 to 2.0
                    .temperature(0.7)
                    .build();

            Prompt prompt = new Prompt(userPrompt, options);
            ChatResponse response = chatModel.call(prompt);

            if (response != null && response.getResult() != null && response.getResult().getOutput() != null) {
                return response.getResult().getOutput().getText();
            }

            return "AI returned an empty response with no specific error.";

        } catch (Exception e) {
            // Log the full technical error to your console
            System.err.println("--- TECHNICAL API ERROR START ---");
            e.printStackTrace();
            System.err.println("--- TECHNICAL API ERROR END ---");

            // Return the EXACT error message from Google/Spring AI
            // This will show things like "404 Not Found" or "429 Quota Exceeded"
            return "TECHNICAL ERROR: " + e.getMessage();
        }
    }
}