package com.itclinkedin.payment.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String idempotencyKey;            // no double-subscribe

    @Column(nullable = false, updatable = false)
    private String requestHash;               // fingerprint of the request body

    private String userId;                    // who owns it
    private String plan;                      // which plan

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status = SubscriptionStatus.PENDING;

    private BigDecimal amount;                // money → BigDecimal, never double
    private String currency;

    private String stripeSubscriptionId;      // link to Stripe
    private Instant currentPeriodEnd;         // renews / expires
    private Instant createdAt = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public void setRequestHash(String requestHash) {
        this.requestHash = requestHash;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStripeSubscriptionId() {
        return stripeSubscriptionId;
    }

    public void setStripeSubscriptionId(String stripeSubscriptionId) {
        this.stripeSubscriptionId = stripeSubscriptionId;
    }

    public Instant getCurrentPeriodEnd() {
        return currentPeriodEnd;
    }

    public void setCurrentPeriodEnd(Instant currentPeriodEnd) {
        this.currentPeriodEnd = currentPeriodEnd;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // ---- STATE MACHINE: only valid moves ----
    public void activate() {
        if (status != SubscriptionStatus.PROCESSING)
            throw new IllegalStateException("Can only activate a PROCESSING subscription, was " + status);
        this.status = SubscriptionStatus.ACTIVE;
    }
    public void markPastDue() {
        if (status != SubscriptionStatus.ACTIVE)
            throw new IllegalStateException("Can only mark ACTIVE as PAST_DUE, was " + status);
        this.status = SubscriptionStatus.PAST_DUE;
    }
    public void cancel() {
        this.status = SubscriptionStatus.CANCELED;
    }


}
