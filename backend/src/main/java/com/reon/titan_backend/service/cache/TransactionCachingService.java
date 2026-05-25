package com.reon.titan_backend.service.cache;

public interface TransactionCachingService {
    Long incrementAndRetrieveCount(String userId);
}