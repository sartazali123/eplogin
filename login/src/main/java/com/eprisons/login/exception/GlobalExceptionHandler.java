package com.eprisons.login.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.security.core.AuthenticationException;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Invalid username, password, disabled account
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request) {

        return build(
                HttpStatus.UNAUTHORIZED,
                "Invalid username or password",
                request
        );
    }

    // @Valid / @NotBlank / @Size failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": "
                        + e.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));

        return build(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
    }

    // Invalid JSON body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleInvalidJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return build(
                HttpStatus.BAD_REQUEST,
                "Malformed or missing JSON request body",
                request
        );
    }

    // Unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(
            Exception ex,
            HttpServletRequest request) {

        // Log full exception server-side with SLF4J
        // Do not expose internal details to API callers.

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal server error occurred",
                request
        );
    }

    private ResponseEntity<ApiError> build(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        return ResponseEntity.status(status).body(
                ApiError.of(
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ApiError> handleDuplicateUser(
            DuplicateUserException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiError.of(
                        409,
                        "Conflict",
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }


}

