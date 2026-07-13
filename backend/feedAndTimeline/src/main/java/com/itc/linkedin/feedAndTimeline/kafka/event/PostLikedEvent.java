package com.itc.linkedin.feedAndTimeline.kafka.event;

import java.time.LocalDateTime;

public record PostLikedEvent(
        Long eventId,
        Long postId,
        String postAuthorId,
        String actorUserId,
        String actorName,
        boolean liked,
        int likesCount,
        LocalDateTime likedAt
) {
    public PostLikedEvent(
            Long eventId,
            Long postId,
            String actorUserId,
            int likesCount,
            LocalDateTime likedAt
    ) {
        this(eventId, postId, null, actorUserId, actorUserId, true, likesCount, likedAt);
    }
}
