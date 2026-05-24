package com.reon.titan_backend.document;

import com.reon.titan_backend.document.type.Status;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
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
    private String userId;
    private Double amount;

    @CreatedDate
    private Instant timestamp;

    private Status status;
}
