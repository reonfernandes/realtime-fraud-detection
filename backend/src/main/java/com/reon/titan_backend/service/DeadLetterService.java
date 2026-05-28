package com.reon.titan_backend.service;

import com.reon.titan_backend.dto.TransactionEvent;

public interface DeadLetterService {
    void handleFailedTransaction(TransactionEvent event, String reason, String topic, Integer partition, Long offset);
}
