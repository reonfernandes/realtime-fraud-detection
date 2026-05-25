package com.reon.titan_backend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    private final String transaction;

    public KafkaConfig(@Value("${security.kafka.topic.transaction}") String transaction) {
        this.transaction = transaction;
    }

    @Bean
    public NewTopic rawTransactionTopic() {
        return TopicBuilder
                .name(transaction)
                .partitions(2)
                .replicas(1)
                .build();
    }
}
