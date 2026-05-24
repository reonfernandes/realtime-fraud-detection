package com.reon.titan_backend.exception.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        String timestamp
) {
    public ApiResponse(boolean success, String message, T data) {
        this(success, message, data, Instant.now().toString());
    }
}
