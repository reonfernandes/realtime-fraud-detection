package com.reon.titan_backend.dto.response;

import com.reon.titan_backend.document.type.Status;
import lombok.Builder;

import java.time.Instant;

@Builder
public record TransactionResponse(
        String transactionId,
        String userId,
        Double amount,
        Status status
) {
}
