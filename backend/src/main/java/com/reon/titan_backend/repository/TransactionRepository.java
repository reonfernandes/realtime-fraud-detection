package com.reon.titan_backend.repository;

import com.reon.titan_backend.document.Transaction;
import com.reon.titan_backend.document.type.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'status': ?1 } }")
    long updateStatus(String transactionId, Status status);

    Page<Transaction> findByUserId(String userId, Pageable pageable);
}
