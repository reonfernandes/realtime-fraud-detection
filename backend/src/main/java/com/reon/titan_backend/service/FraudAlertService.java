package com.reon.titan_backend.service;

import com.reon.titan_backend.dto.TransactionEvent;
import com.reon.titan_backend.dto.response.FraudAlertResponse;

import java.util.List;

public interface FraudAlertService {
    void raiseFraudAlert(TransactionEvent transactionEvent, String reason);
    List<FraudAlertResponse> getAllAlerts(int page, int size);
    List<FraudAlertResponse> getUserAlerts(String userId, int page, int size);
}
