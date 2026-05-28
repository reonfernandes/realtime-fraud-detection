package com.reon.titan_backend.repository;

import com.reon.titan_backend.document.FailedTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedTransactionRepository extends MongoRepository<FailedTransaction, String> {
}
