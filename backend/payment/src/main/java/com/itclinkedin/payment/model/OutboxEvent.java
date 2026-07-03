package com.itclinkedin.payment.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;      // subscription.activated
    private String aggregateId;    // which subscription (as string)

    @Column(length = 1000)
    private String payload;        // JSON the consumer reads

    private String status;         // PENDING / SENT
    private Instant createdAt = Instant.now();

    protected OutboxEvent() { }

    private OutboxEvent(String eventType, String aggregateId, String payload) {
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.status = "PENDING";
    }

    public static OutboxEvent pending(String eventType, String aggregateId, String payload) {
        return new OutboxEvent(eventType, aggregateId, payload);
    }

    public void markSent() {
        this.status = "SENT";
    }

    public Long getId() { return id; }
    public String getEventType() { return eventType; }
    public String getAggregateId() { return aggregateId; }
    public String getPayload() { return payload; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}