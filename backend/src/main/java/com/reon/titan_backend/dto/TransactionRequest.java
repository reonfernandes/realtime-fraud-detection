package com.reon.titan_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record TransactionRequest(
        @NotBlank(message = "User Id is mandatory")
        String userId,

        @NotNull(message = "Amount is required")
                @Positive(message = "Amount must be greater than zero[0]")
        Double amount
) {
}
