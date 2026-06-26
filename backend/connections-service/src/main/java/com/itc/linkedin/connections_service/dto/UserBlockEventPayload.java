package com.itc.linkedin.connections_service.dto;
import java.time.Instant;
import java.util.UUID;

public record UserBlockEventPayload(
        UUID eventId,
        String eventType,
        UUID blockerId,
        UUID blockedId,
        Instant occurredAt
) {
}
