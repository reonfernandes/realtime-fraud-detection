package com.reon.titan_backend.service.impl;

import com.reon.titan_backend.service.TokenBlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * A jwt cannot be taken back once it is handed out, so logout stores its id in redis until
 * the token would have expired anyway. The filter checks this list on every request.
 */
@Service
@Slf4j
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private final String keyPrefix;

    public TokenBlacklistServiceImpl(StringRedisTemplate redisTemplate,
                                     @Value("${security.redis.blacklist.key}") String keyPrefix) {
        this.redisTemplate = redisTemplate;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void blacklist(String tokenId, long secondsUntilExpiry) {
        if (secondsUntilExpiry <= 0) {
            return;
        }

        redisTemplate.opsForValue().set(keyPrefix + ":" + tokenId, "1", Duration.ofSeconds(secondsUntilExpiry));
        log.info("Token blacklisted for the next {}s", secondsUntilExpiry);
    }

    @Override
    public boolean isBlacklisted(String tokenId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(keyPrefix + ":" + tokenId));
    }
}
