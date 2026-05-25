package com.reon.titan_backend.service.cache.impl;

import com.reon.titan_backend.service.cache.TransactionCachingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TransactionCachingServiceImpl implements TransactionCachingService {

    private final StringRedisTemplate redisTemplate;
    private final long windowExpirationSeconds;
    private final String uniqueKey;

    public TransactionCachingServiceImpl(StringRedisTemplate redisTemplate,
                                         @Value("${security.redis.transaction.expiration}") long windowExpirationSeconds,
                                         @Value("${security.redis.transaction.key}") String uniqueKey) {
        this.redisTemplate = redisTemplate;
        this.windowExpirationSeconds = windowExpirationSeconds;
        this.uniqueKey = uniqueKey;
    }

    private String generateUniqueUserTransactionKey(String userId) {
        // redis command set key
        return uniqueKey + ":" + userId;
    }

    private Long incrementTransactionCount(String transactionKey) {
        // redis command INCR key
        return redisTemplate.opsForValue().increment(transactionKey);
    }

    @Override
    public Long incrementAndRetrieveCount(String userId) {
        String key = generateUniqueUserTransactionKey(userId);
        Long currentCount = incrementTransactionCount(key);

        if (currentCount != null && currentCount == 1) {
            redisTemplate.expire(key, windowExpirationSeconds, TimeUnit.SECONDS);
            log.debug("Set user window sliding expiration to {} seconds", windowExpirationSeconds);
        }

        return currentCount != null ? currentCount : 0L;
    }
}
