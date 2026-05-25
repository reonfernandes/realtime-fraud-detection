package com.reon.titan_backend.service.cache.impl;

import com.reon.titan_backend.service.cache.TransactionCachingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

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
    public void cacheUserTransaction(String userId) {
        String key = generateUniqueUserTransactionKey(userId);
        Long currentCount = incrementTransactionCount(key);
        log.info("Incremented window counter for user: {}. Current window count: {}", userId, currentCount);

        if (currentCount != null && currentCount == 1) {
            // redis command expire key seconds
            redisTemplate.expire(key, Duration.ofSeconds(windowExpirationSeconds));
            log.debug("Set user window sliding expiration to {} seconds", windowExpirationSeconds);        }
    }
}
