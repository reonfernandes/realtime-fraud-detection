package com.reon.titan_backend.repository;

import com.reon.titan_backend.document.Transaction;
import com.reon.titan_backend.document.type.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
    @Query("{ '_id':  ?0}")
    void updateStatus(String transactionId, Status status);
}
