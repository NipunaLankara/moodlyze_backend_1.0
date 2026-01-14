package com.example.emotion_service.advisor;

import com.example.emotion_service.exception.EmotionDetectionException;
import com.example.emotion_service.util.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class AppWideExceptionHandler {

    @ExceptionHandler(EmotionDetectionException.class)
    public ResponseEntity<StandardResponse> handleDetectionException(EmotionDetectionException e) {
        return new ResponseEntity<>(
                new StandardResponse(400, e.getMessage(), null),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<StandardResponse> handleMaxSizeException(MaxUploadSizeExceededException e) {
        return new ResponseEntity<>(
                new StandardResponse(413, "File size limit exceeded! Please upload a smaller file.", e.getMessage()),
                HttpStatus.PAYLOAD_TOO_LARGE
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse> handleGeneralException(Exception e) {
        return new ResponseEntity<>(
                new StandardResponse(500, "Internal Server Error", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<StandardResponse> handleFeignException(feign.FeignException e) {
        return new ResponseEntity<>(
                new StandardResponse(503, "AI Service is currently unavailable", e.getMessage()),
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

}