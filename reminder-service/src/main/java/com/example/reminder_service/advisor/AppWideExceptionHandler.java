package com.example.reminder_service.advisor;

import com.example.reminder_service.exception.NotFoundException;
import com.example.reminder_service.util.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppWideExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardResponse> handleNotFoundException(NotFoundException ex) {

        return new ResponseEntity<>(
                new StandardResponse(
                        404,
                        "Not Found Error",
                        ex.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse> handleOtherExceptions(Exception ex) {

        return new ResponseEntity<>(
                new StandardResponse(
                        500,
                        "Internal Server Error",
                        ex.getMessage()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}