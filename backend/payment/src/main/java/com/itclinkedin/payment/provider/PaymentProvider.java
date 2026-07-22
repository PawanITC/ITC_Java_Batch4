package com.itclinkedin.payment.provider;

import java.math.BigDecimal;

public interface PaymentProvider {
    ChargeResult charge(String userId, BigDecimal amount, String currency, String idempotencyKey);
    void refund(String reference);
}