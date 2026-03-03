package com.example.analyze_service.advisor;

import com.example.analyze_service.exception.AnalyzeServiceException;
import com.example.analyze_service.exception.NotFoundException;
import com.example.analyze_service.util.StandardResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppWideExceptionHandler {

    // Handle custom analyze-service errors
    @ExceptionHandler(AnalyzeServiceException.class)
    public ResponseEntity<StandardResponse> handleAnalyzeServiceException(AnalyzeServiceException ex) {
        return new ResponseEntity<>(
                new StandardResponse(400, "Analyze Service Error", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    // Handle not found errors
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardResponse> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(
                new StandardResponse(404, "Not Found Error", ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(
                new StandardResponse(400, "Validation Error", errorMessage),
                HttpStatus.BAD_REQUEST
        );
    }

    // Handle Feign client errors (calling other services)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<StandardResponse> handleFeignException(FeignException ex) {
        return new ResponseEntity<>(
                new StandardResponse(502, "External Service Error", ex.getMessage()),
                HttpStatus.BAD_GATEWAY
        );
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse> handleOtherExceptions(Exception ex) {
        return new ResponseEntity<>(
                new StandardResponse(500, "Internal Server Error", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}