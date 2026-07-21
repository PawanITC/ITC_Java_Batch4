package com.itc.linkedin.searchAndDiscover.kafka.event;

public record ProfileIndexEvent(
        String eventId,
        String eventType,
        int eventVersion,
        String occurredAt,
        String profileId,
        String keycloakUserId,
        String firstName,
        String lastName,
        String email,
        String headline,
        String city,
        String country,
        Boolean profilePublic
) {
}
