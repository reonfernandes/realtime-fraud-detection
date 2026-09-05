package com.reon.titan_backend.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record FailedTransactionResponse(
        String id,
        String transactionId,
        String failureReason,
        Instant failedAt,
        String topic,
        Integer partition,
        Long offset
) {
}
