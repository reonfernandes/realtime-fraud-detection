package com.reon.titan_backend.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record TransactionEvent(
        String transactionId,
        String userId,
        BigDecimal amount,
        Instant transactionTimeStamp
) {
}
