package com.reon.titan_backend.dto.response;

import com.reon.titan_backend.document.type.Status;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record TransactionResponse(
        String transactionId,
        String userId,
        BigDecimal amount,
        Status status
) {
}
