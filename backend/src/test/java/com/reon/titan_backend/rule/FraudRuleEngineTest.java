package com.reon.titan_backend.rule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FraudRuleEngineTest {

    private FraudRuleEngine fraudRuleEngine;

    @BeforeEach
    void setUp() {
        // max 3 per window, high value above 50000, suspicious hours 1 to 4, daily limit 50000
        fraudRuleEngine = new FraudRuleEngine(3, new BigDecimal("50000"), 1, 4, new BigDecimal("50000"));
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
        assertTrue(fraudRuleEngine.isHighValueTransaction(new BigDecimal("60000")));
    }

    @Test
    void amountEqualToLimitIsNotHighValue() {
        assertFalse(fraudRuleEngine.isHighValueTransaction(new BigDecimal("50000.00")));
    }

    @Test
    void oneRupeeOverTheLimitIsHighValue() {
        assertTrue(fraudRuleEngine.isHighValueTransaction(new BigDecimal("50000.01")));
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
        FraudRuleEngine nightRuleEngine = new FraudRuleEngine(3, new BigDecimal("50000"), 23, 4, new BigDecimal("50000"));

        assertTrue(nightRuleEngine.isSuspiciousTime(Instant.parse("2026-05-24T23:30:00Z")));
        assertTrue(nightRuleEngine.isSuspiciousTime(Instant.parse("2026-05-24T01:00:00Z")));
        assertFalse(nightRuleEngine.isSuspiciousTime(Instant.parse("2026-05-24T10:00:00Z")));
    }

    @Test
    void totalAboveDailyLimitIsFlagged() {
        assertTrue(fraudRuleEngine.isDailyLimitExceeded(new BigDecimal("50000.01")));
    }

    @Test
    void totalEqualToDailyLimitIsAllowed() {
        assertFalse(fraudRuleEngine.isDailyLimitExceeded(new BigDecimal("50000")));
    }

    @Test
    void amountsThatAddUpToTheLimitExactlyAreAllowed() {
        // with double this sum can come out slightly above 50000 and get wrongly flagged
        BigDecimal total = new BigDecimal("20000.10").add(new BigDecimal("29999.90"));

        assertFalse(fraudRuleEngine.isDailyLimitExceeded(total));
    }
}
