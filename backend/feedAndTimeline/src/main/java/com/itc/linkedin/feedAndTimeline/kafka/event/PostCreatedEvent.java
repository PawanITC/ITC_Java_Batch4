package com.itc.linkedin.feedAndTimeline.kafka.event;

import java.time.LocalDateTime;

public record PostCreatedEvent(
        String eventId,
        String eventType,
        int eventVersion,
        LocalDateTime occurredAt,
        Long postId,
        String authorId,
        String authorName,
        String authorHeadline,
        String content,
        String mediaObjectKey,
        String mediaType,
        LocalDateTime createdAt
) {
}
