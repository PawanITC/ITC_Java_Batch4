package com.itclinkedin.payment.provider;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class MockPaymentProvider implements PaymentProvider {

    private static final BigDecimal DECLINE_AT = new BigDecimal("1000");
    private static final BigDecimal CHAOS_AT   = new BigDecimal("666");

    @Retry(name = "paymentProvider")
    @CircuitBreaker(name = "paymentProvider")
    @Override
    public ChargeResult charge(String userId, BigDecimal amount, String currency, String idempotencyKey) {
        System.out.println("💳 [MOCK] charging " + amount + " " + currency + " for " + userId);

        // test hook: amount 666 simulates the provider being DOWN (transient) → retry + circuit breaker
        if (amount.compareTo(CHAOS_AT) == 0) {
            System.out.println("💥 [MOCK] provider unavailable (simulated outage)");
            throw new RuntimeException("payment provider timeout (simulated)");
        }

        // business decline (NOT retried — it's a real answer, not a blip)
        if (amount.compareTo(DECLINE_AT) >= 0) {
            System.out.println("❌ [MOCK] declined — amount too high");
            return ChargeResult.declined("insufficient funds (mock)");
        }

        String reference = "mock_ch_" + idempotencyKey;
        System.out.println("✅ [MOCK] charged OK, ref=" + reference);
        return ChargeResult.ok(reference);
    }

    @Override
    public void refund(String reference) {
        System.out.println("↩️ [MOCK] REFUNDED charge " + reference);
    }
}