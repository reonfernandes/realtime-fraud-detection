package com.reon.titan_backend.dto.response;

import com.reon.titan_backend.document.type.TokenType;

import java.time.Instant;

public record SignInResponse(
        String token,
        TokenType tokenType,
        Integer expiry,
        Instant issuedAt
) {
}
