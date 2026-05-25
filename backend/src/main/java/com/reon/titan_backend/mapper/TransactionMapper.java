package com.reon.titan_backend.mapper;

import com.reon.titan_backend.document.Transaction;
import com.reon.titan_backend.dto.TransactionEvent;
import com.reon.titan_backend.dto.TransactionRequest;
import com.reon.titan_backend.dto.response.TransactionResponse;
import com.reon.titan_backend.dto.response.TransactionStatusResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public Transaction mapToEntity(TransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .userId(request.userId())
                .amount(request.amount())
                .build();
        return transaction;
    }

    public TransactionResponse responseToUser(Transaction transaction) {
        TransactionResponse response = TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUserId())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .build();
        return response;
    }

    public TransactionStatusResponse transactionStatusResponse(Transaction transaction) {
        TransactionStatusResponse statusResponse = TransactionStatusResponse.builder()
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUserId())
                .transactionStatus(transaction.getStatus())
                .initialTransactionTime(transaction.getTimestamp())
                .build();
        return statusResponse;
    }

    public TransactionEvent rawTransactionEvent(Transaction transaction) {
        TransactionEvent transactionEvent = TransactionEvent.builder()
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUserId())
                .amount(transaction.getAmount())
                .transactionTimeStamp(transaction.getTimestamp())
                .build();
        return transactionEvent;
    }
}
