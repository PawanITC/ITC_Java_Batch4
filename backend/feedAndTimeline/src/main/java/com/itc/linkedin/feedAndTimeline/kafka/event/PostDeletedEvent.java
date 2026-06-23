package com.itc.linkedin.feedAndTimeline.kafka.event;

import java.time.LocalDateTime;

public record PostDeletedEvent(
        Long eventId,
        Long postId,
        String authorId,
        LocalDateTime deletedAt
) {
}