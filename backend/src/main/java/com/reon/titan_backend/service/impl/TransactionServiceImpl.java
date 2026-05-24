package com.reon.titan_backend.service.impl;

import com.reon.titan_backend.document.Transaction;
import com.reon.titan_backend.document.type.Status;
import com.reon.titan_backend.dto.TransactionRequest;
import com.reon.titan_backend.dto.response.TransactionResponse;
import com.reon.titan_backend.dto.response.TransactionStatusResponse;
import com.reon.titan_backend.exception.TransactionNotFound;
import com.reon.titan_backend.mapper.TransactionMapper;
import com.reon.titan_backend.repository.TransactionRepository;
import com.reon.titan_backend.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public TransactionResponse generateNewTransaction(TransactionRequest transactionRequest) {
        log.info("Processing new transaction for user: {}", transactionRequest.userId());
        Transaction transaction = transactionMapper.mapToEntity(transactionRequest);

        String uniqueTransactionId = UUID.randomUUID().toString();
        transaction.setTransactionId(uniqueTransactionId);
        transaction.setStatus(Status.PENDING);
        transaction.setTimestamp(Instant.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction accepted successfully, tracking id: {}", uniqueTransactionId);

        // event creation when transaction is accepted - kafka

        return transactionMapper.responseToUser(savedTransaction);
    }

    @Override
    public TransactionStatusResponse getTransactionStatus(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(
                () -> new TransactionNotFound("Transaction not found..")
        );
        return transactionMapper.transactionStatusResponse(transaction);
    }
}
