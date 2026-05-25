package com.reon.titan_backend.service.impl;

import com.reon.titan_backend.document.FraudAlert;
import com.reon.titan_backend.dto.TransactionEvent;
import com.reon.titan_backend.mapper.FraudAlertMapper;
import com.reon.titan_backend.repository.FraudAlertRepository;
import com.reon.titan_backend.service.FraudAlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class FraudAlertServiceImpl implements FraudAlertService {

    private final FraudAlertMapper fraudAlertMapper;
    private final FraudAlertRepository fraudAlertRepository;

    public FraudAlertServiceImpl(FraudAlertMapper fraudAlertMapper, FraudAlertRepository fraudAlertRepository) {
        this.fraudAlertMapper = fraudAlertMapper;
        this.fraudAlertRepository = fraudAlertRepository;
    }

    @Override
    public void raiseFraudAlert(TransactionEvent transactionEvent, String reason) {
        log.info("Processing a fraud alert");
        FraudAlert alert = fraudAlertMapper.transactionFraudAlert(transactionEvent);

        String uniqueAlertId = UUID.randomUUID().toString();
        alert.setAlertId(uniqueAlertId);
        alert.setReason(reason);

        fraudAlertRepository.save(alert);
        log.warn("Fraud alert raised for user: {} | transaction: {} | reason: {}",
                transactionEvent.userId(),
                transactionEvent.transactionId(),
                reason
        );
    }
}
