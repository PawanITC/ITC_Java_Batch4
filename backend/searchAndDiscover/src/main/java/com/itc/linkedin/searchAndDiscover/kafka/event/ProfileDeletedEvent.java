package com.itc.linkedin.searchAndDiscover.kafka.event;

public record ProfileDeletedEvent(
        String eventId,
        String eventType,
        int eventVersion,
        String occurredAt,
        String profileId,
        String keycloakUserId
) {
}
