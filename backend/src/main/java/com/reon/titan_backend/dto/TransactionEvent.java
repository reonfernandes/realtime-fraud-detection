package com.reon.titan_backend.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TransactionEvent(
        String transactionId,
        String userId,
        Double amount,
        Instant transactionTimeStamp
) {
}
