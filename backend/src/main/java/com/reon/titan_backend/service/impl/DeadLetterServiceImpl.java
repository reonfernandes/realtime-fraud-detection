package com.reon.titan_backend.service.impl;

import com.reon.titan_backend.document.FailedTransaction;
import com.reon.titan_backend.document.type.Status;
import com.reon.titan_backend.dto.TransactionEvent;
import com.reon.titan_backend.dto.response.FailedTransactionResponse;
import com.reon.titan_backend.exception.TransactionNotFound;
import com.reon.titan_backend.kafka.producer.TransactionProducer;
import com.reon.titan_backend.service.cache.TransactionCachingService;
import com.reon.titan_backend.mapper.FailedTransactionMapper;
import com.reon.titan_backend.repository.FailedTransactionRepository;
import com.reon.titan_backend.service.DeadLetterService;
import com.reon.titan_backend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeadLetterServiceImpl implements DeadLetterService {

    private final FailedTransactionRepository failedTransactionRepository;
    private final TransactionService transactionService;
    private final FailedTransactionMapper failedTransactionMapper;
    private final TransactionProducer transactionProducer;
    private final TransactionCachingService cachingService;

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

    @Override
    public List<FailedTransactionResponse> getFailedTransactions(int page, int size) {
        return failedTransactionRepository.findAll(PageRequest.of(page, size))
                .map(failedTransactionMapper::toResponse)
                .getContent();
    }

    @Override
    public void replayFailedTransaction(String id) {
        FailedTransaction failedTransaction = failedTransactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFound("Failed transaction not found.."));

        String transactionId = failedTransaction.getTransactionId();

        // this event id is still marked as processed in redis, clear it else the consumer will skip it
        cachingService.removeProcessedEvent(transactionId);
        transactionService.updateTransactionStatus(transactionId, Status.PENDING);

        transactionProducer.publishRawTransaction(failedTransaction.getPayload());
        failedTransactionRepository.delete(failedTransaction);

        log.info("Replayed failed transaction: {}", transactionId);
    }
}
