package com.example.auth_service.advisor;


import com.example.auth_service.exception.AlreadyExistsException;
import com.example.auth_service.exception.InvalidOtpException;
import com.example.auth_service.util.StandardResponse;
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

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<StandardResponse> handleInvalidOtp(InvalidOtpException ex) {
        return new ResponseEntity<>(
                new StandardResponse(400, "Invalid OTP", ex.getMessage()), HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<StandardResponse> handleBadCredentials(org.springframework.security.authentication.BadCredentialsException ex) {
        return new ResponseEntity<>(
                new StandardResponse(401, "Login Failed", "Invalid email or password"),
                HttpStatus.UNAUTHORIZED
        );
    }

    // A generic "catch-all" handler is also a good idea for debugging
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse> handleGeneralException(Exception ex) {
        return new ResponseEntity<>(
                new StandardResponse(500, "Internal Server Error", ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
