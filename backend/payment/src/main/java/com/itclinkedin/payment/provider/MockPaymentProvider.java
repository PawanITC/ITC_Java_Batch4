package com.itclinkedin.payment.provider;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class MockPaymentProvider implements PaymentProvider {

    private static final BigDecimal DECLINE_AT = new BigDecimal("1000");

    @Override
    public ChargeResult charge(String userId, BigDecimal amount, String currency, String idempotencyKey) {
        System.out.println("💳 [MOCK] charging " + amount + " " + currency + " for " + userId);

        // test hook: any amount >= 1000 is "declined" so you can PROVE the failure path
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