package com.itclinkedin.payment.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long subscriptionId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String status;              // SUCCEEDED / DECLINED / REFUNDED
    private String providerReference;   // charge ref (null if declined)
    private Instant createdAt = Instant.now();

    protected LedgerEntry() { }   // JPA needs a no-arg constructor

    private LedgerEntry(Long subscriptionId, String userId, BigDecimal amount,
                        String currency, String status, String providerReference) {
        this.subscriptionId = subscriptionId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.providerReference = providerReference;
    }

    // factory methods — a ledger row reads like English, and is IMMUTABLE (no setters)
    public static LedgerEntry succeeded(Subscription sub, String reference) {
        return new LedgerEntry(sub.getId(), sub.getUserId(), sub.getAmount(),
                sub.getCurrency(), "SUCCEEDED", reference);
    }

    public static LedgerEntry declined(Subscription sub) {
        return new LedgerEntry(sub.getId(), sub.getUserId(), sub.getAmount(),
                sub.getCurrency(), "DECLINED", null);
    }

    public Long getId() { return id; }
    public Long getSubscriptionId() { return subscriptionId; }
    public String getUserId() { return userId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public String getProviderReference() { return providerReference; }
    public Instant getCreatedAt() { return createdAt; }
}