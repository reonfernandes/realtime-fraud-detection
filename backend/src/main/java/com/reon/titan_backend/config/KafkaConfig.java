package com.reon.titan_backend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    private final String transaction;
    private final int partitions;

    public KafkaConfig(@Value("${security.kafka.topic.transaction}") String transaction,
                       @Value("${security.kafka.topic.partitions}") int partitions) {
        this.transaction = transaction;
        this.partitions = partitions;
    }

    @Bean
    public NewTopic rawTransactionTopic() {
        return TopicBuilder
                .name(transaction)
                .partitions(partitions)
                .replicas(1)
                .build();
    }
}
