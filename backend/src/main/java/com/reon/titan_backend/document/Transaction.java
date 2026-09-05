package com.reon.titan_backend.document;

import com.reon.titan_backend.document.type.Status;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    @Id
    private String transactionId;
    @Indexed
    private String userId;
    private Double amount;

    private Instant timestamp;

    @Indexed
    private Status status;
}
