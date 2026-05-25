package com.reon.titan_backend.kafka.producer;

import com.reon.titan_backend.dto.TransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TransactionProducer {
    private final String rawTransactionTopic;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public TransactionProducer(@Value("${security.kafka.topic.transaction}") String rawTransactionTopic,
                               KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.rawTransactionTopic = rawTransactionTopic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishRawTransaction(TransactionEvent transactionEvent) {
        log.info("Publishing transaction {} to kafka topic: {}", transactionEvent.userId(), rawTransactionTopic);

        kafkaTemplate.send(rawTransactionTopic, transactionEvent.userId(), transactionEvent)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        log.info("Successfully send transaction offset [{}]", result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to deliver transaction payload to kafka", exception);
                    }
                });
    }

}
