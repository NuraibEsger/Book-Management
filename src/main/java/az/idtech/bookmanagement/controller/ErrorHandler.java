package az.idtech.bookmanagement.controller;

import az.idtech.bookmanagement.exceptions.NotFoundException;
import az.idtech.bookmanagement.model.responses.ErrorResponse;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static az.idtech.bookmanagement.model.enums.ErrorMessage.INTERNAL_ERROR;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(value = {BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handle(BadCredentialsException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("UNAUTHORIZED")
                .message("Invalid username or password")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(UsernameNotFoundException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("UNAUTHORIZED")
                .message("Invalid username or password")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException exception) {
        List<String> validationErrors = new ArrayList<>();
        exception.getFieldErrors().forEach(error -> validationErrors.add(error.getDefaultMessage()));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("VALIDATION_ERROR")
                .message("Validation failed for one or more fields.")
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("BAD_REQUEST")
                .message("Malformed request body")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(NotFoundException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(exception.getCode())
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handle(IllegalArgumentException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("BAD_REQUEST")
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> handle(Exception exception) {
        ErrorResponse response = ErrorResponse.builder()
                .code(INTERNAL_ERROR.name())
                .message(INTERNAL_ERROR.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
