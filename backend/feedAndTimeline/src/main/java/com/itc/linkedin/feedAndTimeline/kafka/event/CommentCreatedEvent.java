package com.itc.linkedin.feedAndTimeline.kafka.event;

import java.time.LocalDateTime;

public record CommentCreatedEvent(
        Long eventId,
        Long commentId,
        Long postId,
        String postAuthorId,
        String actorUserId,
        String actorName,
        int commentsCount,
        LocalDateTime createdAt
) {
    public CommentCreatedEvent(
            Long eventId,
            Long commentId,
            Long postId,
            String actorUserId,
            String actorName,
            int commentsCount,
            LocalDateTime createdAt
    ) {
        this(eventId, commentId, postId, null, actorUserId, actorName, commentsCount, createdAt);
    }
}
