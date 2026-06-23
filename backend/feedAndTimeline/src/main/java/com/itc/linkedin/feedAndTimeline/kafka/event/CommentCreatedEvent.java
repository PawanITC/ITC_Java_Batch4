package com.itc.linkedin.feedAndTimeline.kafka.event;

import java.time.LocalDateTime;

public record CommentCreatedEvent(
        Long eventId,
        Long commentId,
        Long postId,
        String authorId,
        String authorName,
        int commentsCount,
        LocalDateTime createdAt
) {
}