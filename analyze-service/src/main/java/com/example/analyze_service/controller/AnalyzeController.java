package com.example.analyze_service.controller;

import com.example.analyze_service.dto.AnalysisResponseDTO;
import com.example.analyze_service.service.AnalyzeService;
import com.example.analyze_service.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analyze")
@RequiredArgsConstructor
@CrossOrigin
public class AnalyzeController {

    private final AnalyzeService analyzeService;

    @GetMapping("/process")
    public ResponseEntity<StandardResponse> getMoodBasedAnalysis(
            @RequestHeader("X-User-Id") int userId
    ) {
        AnalysisResponseDTO response = analyzeService.processUserStatus(userId);

        String message = response.getState().equals("REST_REQUIRED")
                ? "User needs rest before working."
                : "User is ready to focus on tasks.";

        return new ResponseEntity<>(
                new StandardResponse(200, message, response),
                HttpStatus.OK
        );
    }
}