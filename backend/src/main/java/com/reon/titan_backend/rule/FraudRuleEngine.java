package com.reon.titan_backend.rule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;

@Component
public class FraudRuleEngine {
    private final int MAX_TRANSACTION_PER_WINDOW;
    private final double HIGH_VALUE_LIMIT;
    private final int SUSPICIOUS_START_HOUR;
    private final int SUSPICIOUS_END_HOUR;

    public FraudRuleEngine(
            @Value("${security.transactions.max-count}") int maxTransactionPerWindow,
            @Value("${security.transactions.high-value-limit}") double highValueLimit,
            @Value("${security.transactions.suspicious-window.start-hour}") int suspiciousStartHour,
            @Value("${security.transactions.suspicious-window.end-hour}") int suspiciousEndHour
    ) {
        MAX_TRANSACTION_PER_WINDOW = maxTransactionPerWindow;
        HIGH_VALUE_LIMIT = highValueLimit;
        SUSPICIOUS_START_HOUR = suspiciousStartHour;
        SUSPICIOUS_END_HOUR = suspiciousEndHour;
    }

    public boolean hasWindowLimitExceeded(Long currentWindowCount) {
        return currentWindowCount > MAX_TRANSACTION_PER_WINDOW;
    }

    public boolean isHighValueTransaction(Double amount) {
        return amount != null && amount > HIGH_VALUE_LIMIT;
    }

    public boolean isSuspiciousTime(Instant timestamp) {
        if (timestamp == null) return false;
        int hour = timestamp.atZone(ZoneOffset.UTC).getHour();
        
        if (SUSPICIOUS_START_HOUR <= SUSPICIOUS_END_HOUR) {
            return hour >= SUSPICIOUS_START_HOUR && hour <= SUSPICIOUS_END_HOUR;
        } else {
            // Handles overnight window, e.g., 23 to 04
            return hour >= SUSPICIOUS_START_HOUR || hour <= SUSPICIOUS_END_HOUR;
        }
    }
}
