package com.reon.titan_backend.mapper;

import com.reon.titan_backend.document.FailedTransaction;
import com.reon.titan_backend.dto.TransactionEvent;
import com.reon.titan_backend.dto.response.FailedTransactionResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class FailedTransactionMapper {

    public FailedTransaction toEntity(TransactionEvent event, String reason, String topic, Integer partition, Long offset) {
        return FailedTransaction.builder()
                .transactionId(event.transactionId())
                .payload(event)
                .failureReason(reason)
                .failedAt(Instant.now())
                .topic(topic)
                .partition(partition)
                .offset(offset)
                .build();
    }

    public FailedTransactionResponse toResponse(FailedTransaction failedTransaction) {
        return FailedTransactionResponse.builder()
                .id(failedTransaction.getId())
                .transactionId(failedTransaction.getTransactionId())
                .failureReason(failedTransaction.getFailureReason())
                .failedAt(failedTransaction.getFailedAt())
                .topic(failedTransaction.getTopic())
                .partition(failedTransaction.getPartition())
                .offset(failedTransaction.getOffset())
                .build();
    }
}
