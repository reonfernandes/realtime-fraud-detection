package com.reon.titan_backend.mapper;

import com.reon.titan_backend.document.FraudAlert;
import com.reon.titan_backend.dto.TransactionEvent;
import org.springframework.stereotype.Component;

@Component
public class FraudAlertMapper {
    public FraudAlert transactionFraudAlert(TransactionEvent transactionEvent) {
        FraudAlert fraudAlert = FraudAlert.builder()
                .targetTransactionId(transactionEvent.transactionId())
                .userId(transactionEvent.userId())
                .flaggedAt(transactionEvent.transactionTimeStamp())
                .build();
        return fraudAlert;
    }
}
