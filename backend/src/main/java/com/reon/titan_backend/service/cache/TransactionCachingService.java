package com.reon.titan_backend.service.cache;

public interface TransactionCachingService {
    void cacheUserTransaction(String userId);
}