package com.itclinkedin.userprofile.events;

public record ProfileDeletedEvent(
        String eventId,
        String eventType,
        int eventVersion,
        String occurredAt,
        String profileId,
        String keycloakUserId
) {
}
