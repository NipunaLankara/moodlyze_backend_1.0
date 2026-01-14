package com.example.analyze_service.service;

import com.example.analyze_service.dto.AnalysisResponseDTO;

public interface AnalyzeService {
    AnalysisResponseDTO processUserStatus(int userId);
}
