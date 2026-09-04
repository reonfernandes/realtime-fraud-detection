package com.reon.titan_backend.service.cache;

public interface TransactionCachingService {
    Long incrementAndRetrieveCount(String userId);
    Double incrementAndRetrieveDailySum(String userId, Double amount);
    Double getDailySum(String userId);
    boolean isDuplicateEvent(String transactionId);
    void removeProcessedEvent(String transactionId);
}
