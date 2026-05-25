package com.reon.titan_backend.rule;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FraudRuleEngine {
    private final int MAX_TRANSACTION_PER_WINDOW;

    public FraudRuleEngine(@Value("${security.transactions.max-count}") int maxTransactionPerWindow) {
        MAX_TRANSACTION_PER_WINDOW = maxTransactionPerWindow;
    }

    public boolean hasWindowLimitExceeded(Long currentWindowCount) {
        return currentWindowCount > MAX_TRANSACTION_PER_WINDOW;
    }
}
