package com.itc.linkedin.postandtimeline.kafka.event;

import java.time.LocalDateTime;

public record PostLikedEvent(
        Long eventId,
        Long postId,
        String postAuthorId,
        String actorUserId,
        String actorName,
        int likesCount,
        LocalDateTime likedAt
) {}
