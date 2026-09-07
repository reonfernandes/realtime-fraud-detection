package com.reon.titan_backend.service.impl;

import com.reon.titan_backend.exception.RateLimitExceededException;
import com.reon.titan_backend.service.RateLimiterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class RateLimiterServiceImpl implements RateLimiterService {

    private final StringRedisTemplate redisRateLimitTemplate;
    private final long windowSeconds;
    private final int maxRequests;
    private final String keyPrefix;
    private final String authKeyPrefix;
    private final long authWindowSeconds;
    private final int authMaxRequests;

    public RateLimiterServiceImpl(
            StringRedisTemplate redisRateLimitTemplate,
            @Value("${security.redis.ratelimit.expiration}") long windowSeconds,
            @Value("${security.redis.ratelimit.max-requests}") int maxRequests,
            @Value("${security.redis.ratelimit.key}") String keyPrefix,
            @Value("${security.redis.authratelimit.key}") String authKeyPrefix,
            @Value("${security.redis.authratelimit.expiration}") long authWindowSeconds,
            @Value("${security.redis.authratelimit.max-requests}") int authMaxRequests
    ) {
        this.redisRateLimitTemplate = redisRateLimitTemplate;
        this.windowSeconds = windowSeconds;
        this.maxRequests = maxRequests;
        this.keyPrefix = keyPrefix;
        this.authKeyPrefix = authKeyPrefix;
        this.authWindowSeconds = authWindowSeconds;
        this.authMaxRequests = authMaxRequests;
    }

    @Override
    public void enforceRateLimit(String userId) {
        String key = keyPrefix + ":" + userId;

        // create the key along with its ttl first, otherwise the expiry can get skipped
        redisRateLimitTemplate.opsForValue().setIfAbsent(key, "0", Duration.ofSeconds(windowSeconds));
        Long requestCount = redisRateLimitTemplate.opsForValue().increment(key);

        if (requestCount != null && requestCount > maxRequests) {
            log.warn("Rate limit breached for user: {} | count: {}", userId, requestCount);
            throw new RateLimitExceededException(
                    "Rate limit exceeded. Max " + maxRequests + " requests per " + windowSeconds + "s."
            );
        }
    }

    /**
     * The normal limit is per user, which does nothing on signup because there is no user yet
     * and anyone can keep making new accounts. So the auth endpoints are limited per ip instead.
     */
    @Override
    public void enforceAuthRateLimit(String ipAddress) {
        String key = authKeyPrefix + ":" + ipAddress;

        redisRateLimitTemplate.opsForValue().setIfAbsent(key, "0", Duration.ofSeconds(authWindowSeconds));
        Long requestCount = redisRateLimitTemplate.opsForValue().increment(key);

        if (requestCount != null && requestCount > authMaxRequests) {
            log.warn("Auth rate limit breached for ip: {} | count: {}", ipAddress, requestCount);
            throw new RateLimitExceededException(
                    "Too many attempts. Max " + authMaxRequests + " per " + authWindowSeconds + "s."
            );
        }
    }
}
