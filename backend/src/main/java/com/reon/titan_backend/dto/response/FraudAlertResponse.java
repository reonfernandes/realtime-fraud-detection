package com.reon.titan_backend.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record FraudAlertResponse(
        String alertId,
        String targetTransactionId,
        String userId,
        String reason,
        Instant flaggedAt
) {
}
