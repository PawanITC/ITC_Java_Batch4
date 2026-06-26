package com.itc.linkedin.connections_service.dto;
import java.time.Instant;
import java.util.UUID;

public record ConnectionRequestAcceptedEvent(
        UUID eventId,
        String eventType,
        UUID connectionId,
        UUID requesterId,
        UUID receiverId,
        Instant occurredAt
) {
}