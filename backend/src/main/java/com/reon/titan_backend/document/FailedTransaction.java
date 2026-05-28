package com.reon.titan_backend.document;

import com.reon.titan_backend.dto.TransactionEvent;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "failed_transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FailedTransaction {
    @Id
    private String id;
    private String transactionId;
    private TransactionEvent payload;
    private String failureReason;
    private Instant failedAt;
    private String topic;
    private Integer partition;
    private Long offset;
}
