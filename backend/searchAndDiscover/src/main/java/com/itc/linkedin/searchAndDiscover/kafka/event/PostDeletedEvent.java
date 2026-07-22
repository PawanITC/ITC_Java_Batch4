package com.itc.linkedin.searchAndDiscover.kafka.event;

import java.time.LocalDateTime;

public record PostDeletedEvent(
        Long eventId,
        Long postId,
        String authorId,
        LocalDateTime deletedAt
) {
}
