package com.reon.titan_backend.service.cache;

import java.math.BigDecimal;

public interface TransactionCachingService {
    Long incrementAndRetrieveCount(String userId);
    void incrementDailySum(String userId, BigDecimal amount);
    BigDecimal getDailySum(String userId);
    boolean isDuplicateEvent(String transactionId);
    void removeProcessedEvent(String transactionId);
}
