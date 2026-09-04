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

    private final String dailySumKey;
    private final Long dailySumKeyExpiration;

    private final String processedKey;
    private final long processedKeyExpiration;

    public TransactionCachingServiceImpl(StringRedisTemplate redisTemplate,
                                         @Value("${security.redis.transaction.expiration}") long windowExpirationSeconds,
                                         @Value("${security.redis.transaction.key}") String uniqueKey,
                                         @Value("${security.transactions.daily-sum.key}") String dailySumKey,
                                         @Value("${security.transactions.daily-sum.expiration-days}") Long dailySumKeyExpiration,
                                         @Value("${security.redis.processed.key}") String processedKey,
                                         @Value("${security.redis.processed.expiration}") long processedKeyExpiration) {
        this.redisTemplate = redisTemplate;
        this.windowExpirationSeconds = windowExpirationSeconds;
        this.uniqueKey = uniqueKey;
        this.dailySumKey = dailySumKey;
        this.dailySumKeyExpiration = dailySumKeyExpiration;
        this.processedKey = processedKey;
        this.processedKeyExpiration = processedKeyExpiration;
    }

    private String generateUniqueUserTransactionKey(String userId) {
        // redis command set key
        return uniqueKey + ":" + userId;
    }

    @Override
    public Long incrementAndRetrieveCount(String userId) {
        String key = generateUniqueUserTransactionKey(userId);

        // set the key with ttl first so the window always expires
        redisTemplate.opsForValue().setIfAbsent(key, "0", Duration.ofSeconds(windowExpirationSeconds));
        Long currentCount = redisTemplate.opsForValue().increment(key);

        return currentCount != null ? currentCount : 0L;
    }

    @Override
    public Double incrementAndRetrieveDailySum(String userId, Double amount) {
        String key = dailySumKey + ":" + userId;

        redisTemplate.opsForValue().setIfAbsent(key, "0", Duration.ofDays(dailySumKeyExpiration));
        Double currentTotal = redisTemplate.opsForValue().increment(key, amount);

        return currentTotal != null ? currentTotal : 0.0;
    }

    @Override
    public Double getDailySum(String userId) {
        String currentTotal = redisTemplate.opsForValue().get(dailySumKey + ":" + userId);
        return currentTotal != null ? Double.parseDouble(currentTotal) : 0.0;
    }

    @Override
    public boolean isDuplicateEvent(String transactionId) {
        String key = processedKey + ":" + transactionId;

        // setIfAbsent gives false when the key is already there, means we already handled this event
        Boolean firstTime = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofSeconds(processedKeyExpiration));

        return firstTime == null || !firstTime;
    }

    @Override
    public void removeProcessedEvent(String transactionId) {
        redisTemplate.delete(processedKey + ":" + transactionId);
    }
}
