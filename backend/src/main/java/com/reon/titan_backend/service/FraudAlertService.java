package com.reon.titan_backend.service;

import com.reon.titan_backend.dto.TransactionEvent;

public interface FraudAlertService {
    void raiseFraudAlert(TransactionEvent transactionEvent, String reason);
}
