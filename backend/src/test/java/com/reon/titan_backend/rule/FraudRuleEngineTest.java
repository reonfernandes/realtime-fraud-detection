package com.reon.titan_backend.rule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FraudRuleEngineTest {

    private FraudRuleEngine fraudRuleEngine;

    @BeforeEach
    void setUp() {
        // max 3 per window, high value above 50000, suspicious hours 1 to 4, daily limit 50000
        fraudRuleEngine = new FraudRuleEngine(3, 50000.0, 1, 4, 50000.0);
    }

    @Test
    void countEqualToMaxIsAllowed() {
        assertFalse(fraudRuleEngine.hasWindowLimitExceeded(3L));
    }

    @Test
    void countAboveMaxIsFlagged() {
        assertTrue(fraudRuleEngine.hasWindowLimitExceeded(4L));
    }

    @Test
    void amountAboveLimitIsHighValue() {
        assertTrue(fraudRuleEngine.isHighValueTransaction(60000.0));
    }

    @Test
    void amountEqualToLimitIsNotHighValue() {
        assertFalse(fraudRuleEngine.isHighValueTransaction(50000.0));
    }

    @Test
    void nullAmountIsNotHighValue() {
        assertFalse(fraudRuleEngine.isHighValueTransaction(null));
    }

    @Test
    void transactionAtTwoAmIsSuspicious() {
        assertTrue(fraudRuleEngine.isSuspiciousTime(Instant.parse("2026-05-24T02:00:00Z")));
    }

    @Test
    void transactionAtEndHourIsNotSuspicious() {
        assertFalse(fraudRuleEngine.isSuspiciousTime(Instant.parse("2026-05-24T04:00:00Z")));
    }

    @Test
    void transactionAtMiddayIsNotSuspicious() {
        assertFalse(fraudRuleEngine.isSuspiciousTime(Instant.parse("2026-05-24T12:00:00Z")));
    }

    @Test
    void nullTimestampIsNotSuspicious() {
        assertFalse(fraudRuleEngine.isSuspiciousTime(null));
    }

    @Test
    void overnightWindowWorksAfterMidnight() {
        FraudRuleEngine nightRuleEngine = new FraudRuleEngine(3, 50000.0, 23, 4, 50000.0);

        assertTrue(nightRuleEngine.isSuspiciousTime(Instant.parse("2026-05-24T23:30:00Z")));
        assertTrue(nightRuleEngine.isSuspiciousTime(Instant.parse("2026-05-24T01:00:00Z")));
        assertFalse(nightRuleEngine.isSuspiciousTime(Instant.parse("2026-05-24T10:00:00Z")));
    }

    @Test
    void totalAboveDailyLimitIsFlagged() {
        assertTrue(fraudRuleEngine.isDailyLimitExceeded(50001.0));
    }

    @Test
    void totalEqualToDailyLimitIsAllowed() {
        assertFalse(fraudRuleEngine.isDailyLimitExceeded(50000.0));
    }
}
