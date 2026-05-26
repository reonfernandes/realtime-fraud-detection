package com.reon.titan_backend.service;

public interface RateLimiterService {
    void enforceRateLimit(String userId);
}