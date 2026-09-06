package com.reon.titan_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TransactionRequest(
        @NotNull(message = "Amount is required")
                @Positive(message = "Amount must be greater than zero[0]")
        BigDecimal amount
) {
}
