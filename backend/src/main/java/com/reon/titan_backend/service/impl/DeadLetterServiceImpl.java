package com.reon.titan_backend.service.impl;

import com.reon.titan_backend.document.FailedTransaction;
import com.reon.titan_backend.document.type.Status;
import com.reon.titan_backend.dto.TransactionEvent;
import com.reon.titan_backend.mapper.FailedTransactionMapper;
import com.reon.titan_backend.repository.FailedTransactionRepository;
import com.reon.titan_backend.service.DeadLetterService;
import com.reon.titan_backend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeadLetterServiceImpl implements DeadLetterService {

    private final FailedTransactionRepository failedTransactionRepository;
    private final TransactionService transactionService;
    private final FailedTransactionMapper failedTransactionMapper;

    @Override
    public void handleFailedTransaction(TransactionEvent event, String reason, String topic, Integer partition, Long offset) {
        log.error("Handling failed transaction: {} from topic: {} | partition: {} | offset: {} | reason: {}",
                event.transactionId(), topic, partition, offset, reason);

        FailedTransaction failedTransaction = failedTransactionMapper.toEntity(event, reason, topic, partition, offset);

        failedTransactionRepository.save(failedTransaction);

        // Update original transaction status if appropriate
        try {
            transactionService.updateTransactionStatus(event.transactionId(), Status.FAILED);
            log.info("Transaction status updated to FAILED for transactionId: {}", event.transactionId());
        } catch (Exception e) {
            log.error("Failed to update transaction status for {}: {}", event.transactionId(), e.getMessage());
        }

        log.info("Failed transaction event persisted to MongoDB for inspection. ID: {}", failedTransaction.getTransactionId());
    }
}
