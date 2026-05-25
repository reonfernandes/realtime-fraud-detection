package com.reon.titan_backend.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "fraud_alerts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FraudAlert {
    @Id
    private String alertId;
    private String targetTransactionId;
    private String userId;
    private String reason;
    private Instant flaggedAt;
}
