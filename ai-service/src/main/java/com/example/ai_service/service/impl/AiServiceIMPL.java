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
            // We set the model here to match the property change
            GoogleGenAiChatOptions options = GoogleGenAiChatOptions.builder()
                    .model("gemini-flash-latest") // This alias is more stable for v1beta
                    .temperature(0.7)
                    .build();

            ChatResponse response = chatModel.call(new Prompt(userPrompt, options));

            if (response != null && response.getResult() != null && response.getResult().getOutput() != null) {
                return response.getResult().getOutput().getText();
            }

            return "AI response was empty. Please check your prompt or API status.";

        } catch (Exception e) {
            // Log the full error to help us see if it's still a 404 or a 429
            System.err.println("AI Service Error: " + e.getMessage());

            if (e.getMessage().contains("404")) {
                return "Error: Model not found. Try changing the model to 'gemini-2.0-flash'.";
            }
            if (e.getMessage().contains("429")) {
                return "Error: Rate limit exceeded. Please wait a minute.";
            }

            return "Error generating response: " + e.getMessage();
        }
    }
}