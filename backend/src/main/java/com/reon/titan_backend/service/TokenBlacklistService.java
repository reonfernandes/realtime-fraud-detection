package com.reon.titan_backend.service;

public interface TokenBlacklistService {
    void blacklist(String tokenId, long secondsUntilExpiry);
    boolean isBlacklisted(String tokenId);
}
