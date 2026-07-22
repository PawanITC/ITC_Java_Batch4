package com.itclinkedin.notification.event;

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
}
