package com.example.task_service.advisor;


import com.example.task_service.exception.AlreadyExistsException;
import com.example.task_service.exception.TaskServiceException;
import com.example.task_service.utill.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppWideExceptionHandler {

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<StandardResponse> handleAlreadyExistsException(AlreadyExistsException exception) {
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(409, "Error", exception.getMessage()), HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ResponseEntity<>(
                new StandardResponse(400, "Validation Error", errorMessage), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(TaskServiceException.class)
    public ResponseEntity<StandardResponse> handleTaskServiceException(TaskServiceException ex) {
        return new ResponseEntity<>(
                new StandardResponse(400, "Task Error", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse> handleOtherExceptions(Exception ex) {
        return new ResponseEntity<>(
                new StandardResponse(500, "Internal Server Error", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
