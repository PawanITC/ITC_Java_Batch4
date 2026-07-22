package com.itc.premiumsubscription.model;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.UUID;


@Entity
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(name = "start_date")
    private ZonedDateTime startDate;

    @Column(name = "end_date")
    private ZonedDateTime endDate;

    @Column(name = "next_billing_date")
    private ZonedDateTime nextBillingDate;

    // Getters, Setters, and Constructors
    public Subscription() {}

    public Subscription(UUID userId, UUID planId, SubscriptionStatus status) {
        this.userId = userId;
        this.planId = planId;
        this.status = status;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getPlanId() { return planId; }
    public void setPlanId(UUID planId) { this.planId = planId; }

    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }

    public ZonedDateTime getStartDate() { return startDate; }
    public void setStartDate(ZonedDateTime startDate) { this.startDate = startDate; }

    public ZonedDateTime getEndDate() { return endDate; }
    public void setEndDate(ZonedDateTime endDate) { this.endDate = endDate; }

    public ZonedDateTime getNextBillingDate() { return nextBillingDate; }
    public void setNextBillingDate(ZonedDateTime nextBillingDate) { this.nextBillingDate = nextBillingDate; }
}
