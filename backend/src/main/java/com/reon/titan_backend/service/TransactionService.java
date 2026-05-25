package com.reon.titan_backend.service;

import com.reon.titan_backend.document.type.Status;
import com.reon.titan_backend.dto.TransactionRequest;
import com.reon.titan_backend.dto.response.TransactionResponse;
import com.reon.titan_backend.dto.response.TransactionStatusResponse;

public interface TransactionService {
    TransactionResponse generateNewTransaction(TransactionRequest transactionRequest);
    TransactionStatusResponse getTransactionStatus(String transactionId);

    void updateTransactionStatus(String transactionId, Status status);
}