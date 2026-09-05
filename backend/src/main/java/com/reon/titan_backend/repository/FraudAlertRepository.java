package com.reon.titan_backend.repository;

import com.reon.titan_backend.document.FraudAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudAlertRepository extends MongoRepository<FraudAlert, String> {
    Page<FraudAlert> findByUserId(String userId, Pageable pageable);
}
