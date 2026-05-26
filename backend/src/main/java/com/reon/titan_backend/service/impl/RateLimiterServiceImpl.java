package com.reon.titan_backend.service.impl;

import com.reon.titan_backend.exception.RateLimitExceededException;
import com.reon.titan_backend.service.RateLimiterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RateLimiterServiceImpl implements RateLimiterService {

    private final StringRedisTemplate redisRateLimitTemplate;
    private final long windowSeconds;
    private final int maxRequests;
    private final String keyPrefix;

    public RateLimiterServiceImpl(
            StringRedisTemplate redisRateLimitTemplate,
            @Value("${security.redis.ratelimit.expiration}") long windowSeconds,
            @Value("${security.redis.ratelimit.max-requests}") int maxRequests,
            @Value("${security.redis.ratelimit.key}") String keyPrefix
    ) {
        this.redisRateLimitTemplate = redisRateLimitTemplate;
        this.windowSeconds = windowSeconds;
        this.maxRequests = maxRequests;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void enforceRateLimit(String userId) {
        String key = keyPrefix + ":" + userId;
        Long requestCount = redisRateLimitTemplate.opsForValue().increment(key);

        if (requestCount != null && requestCount == 1) {
            redisRateLimitTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);
        }

        if (requestCount != null && requestCount > maxRequests) {
            log.warn("Rate limit breached for user: {} | count: {}", userId, requestCount);
            throw new RateLimitExceededException(
                    "Rate limit exceeded. Max " + maxRequests + " requests per " + windowSeconds + "s."
            );
        }
    }
}
