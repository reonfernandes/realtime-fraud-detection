package com.reon.titan_backend.exception;

import com.reon.titan_backend.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Validation failed", errors));
    }

    @ExceptionHandler(TransactionNotFound.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleTransactionNotFoundException(TransactionNotFound exception) {
        Map<String, String> error = new HashMap<>();
        error.put("transaction", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, "Transaction not found", error));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleRateLimitException(RateLimitExceededException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("request limit", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ApiResponse<>(false, "Too many requests", error));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleOtherExceptions(Exception exception) {
        log.error("Unexpected error", exception);

        Map<String, String> error = new HashMap<>();
        error.put("error", "Something went wrong, please try again later");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Request failed", error));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleEmailAlreadyExists(EmailAlreadyExistsException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("email", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(false, "Account already exists", error));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleAuthenticationException(AuthenticationException exception) {
        log.warn("Failed sign in attempt: {}", exception.getMessage());

        Map<String, String> error = new HashMap<>();
        // never reveal WHICH half was wrong, that tells an attacker the email exists
        error.put("credentials", "Invalid email or password");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "Authentication failed", error));
    }
}
