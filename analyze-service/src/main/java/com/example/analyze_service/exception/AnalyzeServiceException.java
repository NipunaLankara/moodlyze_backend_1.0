package com.example.analyze_service.exception;

public class AnalyzeServiceException extends RuntimeException {
    public AnalyzeServiceException(String message) {
        super(message);
    }

    public AnalyzeServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}