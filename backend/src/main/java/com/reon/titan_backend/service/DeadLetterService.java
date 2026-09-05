package com.reon.titan_backend.service;

import com.reon.titan_backend.dto.TransactionEvent;
import com.reon.titan_backend.dto.response.FailedTransactionResponse;

import java.util.List;

public interface DeadLetterService {
    void handleFailedTransaction(TransactionEvent event, String reason, String topic, Integer partition, Long offset);
    List<FailedTransactionResponse> getFailedTransactions(int page, int size);
    void replayFailedTransaction(String id);
}
