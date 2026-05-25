package com.reon.titan_backend.kafka.consumer;

import com.reon.titan_backend.document.type.Status;
import com.reon.titan_backend.dto.TransactionEvent;
import com.reon.titan_backend.rule.FraudRuleEngine;
import com.reon.titan_backend.service.FraudAlertService;
import com.reon.titan_backend.service.TransactionService;
import com.reon.titan_backend.service.cache.TransactionCachingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionConsumer {

    private final TransactionCachingService cachingService;
    private final FraudRuleEngine fraudRuleEngine;
    private final FraudAlertService fraudAlertService;
    private final TransactionService transactionService;

    public TransactionConsumer(TransactionCachingService cachingService, FraudRuleEngine fraudRuleEngine, FraudAlertService fraudAlertService, TransactionService transactionService) {
        this.cachingService = cachingService;
        this.fraudRuleEngine = fraudRuleEngine;
        this.fraudAlertService = fraudAlertService;
        this.transactionService = transactionService;
    }

    /**
     * @param transactionEvent
     * This Consumer will consume records from kafka and call the TransactionCachingService for in-memory caching
     * Likewise it becomes entry point for 3rd phase where transaction will be evaluated corresponding to fraud detection
     * rule set by the system.
     *
     * We will manually handle the "bad" or "unprocessable message" using custom dlt
     * There will be 4 attempts of trying to process a record, each time the process fails, there will be a delay of 2s
     * and multiply by 2s.
     *
     * If all attempts fail the record will be sent to DLT where the record will be stored in Database for manual inspection
     * and raising alerts
     */

    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(delay = 2000, multiplier = 2.0),
            dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR,
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = "${security.kafka.topic.transaction}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void transactionWorkerEngine(TransactionEvent transactionEvent) {
        log.info("Consuming transaction event: {}", transactionEvent.transactionId());

        Long currentCount = cachingService.incrementAndRetrieveCount(transactionEvent.userId());
        boolean isFraud = fraudRuleEngine.hasWindowLimitExceeded(currentCount);

        if (isFraud) {
            fraudAlertService.raiseFraudAlert(transactionEvent, "Velocity threshold exceeded.");
            transactionService.updateTransactionStatus(transactionEvent.transactionId(), Status.FRAUDULENT);
        } else {
            transactionService.updateTransactionStatus(transactionEvent.transactionId(), Status.APPROVED);
        }

        log.info("Fraud check result for user: {} | isFraud: {}", transactionEvent.userId(), isFraud);
    }
}
