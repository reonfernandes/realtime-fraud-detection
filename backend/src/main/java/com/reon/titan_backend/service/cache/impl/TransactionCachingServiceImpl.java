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

    private final Long dailySumKeyExpiration;

    public TransactionCachingServiceImpl(StringRedisTemplate redisTemplate,
                                         @Value("${security.redis.transaction.expiration}") long windowExpirationSeconds,
                                         @Value("${security.redis.transaction.key}") String uniqueKey,
                                         @Value("${security.transactions.daily-sum.expiration-days}") Long dailySumKeyExpiration) {
        this.redisTemplate = redisTemplate;
        this.windowExpirationSeconds = windowExpirationSeconds;
        this.uniqueKey = uniqueKey;
        this.dailySumKeyExpiration = dailySumKeyExpiration;
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

    @Override
    public Double incrementAndRetrieveDailySum(String userId, Double amount) {
        String key = "user:daily:sum:" + userId;
        Double currentTotal = redisTemplate.opsForValue().increment(key, amount);

        if (currentTotal != null && currentTotal.equals(amount)) {
            redisTemplate.expire(key, dailySumKeyExpiration, TimeUnit.DAYS);
            log.debug("Set daily sum expiration for user {}: {} days", userId, dailySumKeyExpiration);
        }

        return currentTotal != null ? currentTotal : 0.0;
    }
}
