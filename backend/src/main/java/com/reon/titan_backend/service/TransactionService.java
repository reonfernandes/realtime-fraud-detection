package com.reon.titan_backend.service;

import com.reon.titan_backend.document.type.Status;
import com.reon.titan_backend.dto.TransactionRequest;
import com.reon.titan_backend.dto.response.TransactionResponse;
import com.reon.titan_backend.dto.response.TransactionStatusResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse generateNewTransaction(TransactionRequest transactionRequest, String userId);
    TransactionStatusResponse getTransactionStatus(String transactionId);
    List<TransactionResponse> getUserTransactions(String userId, int page, int size);

    void updateTransactionStatus(String transactionId, Status status);
}