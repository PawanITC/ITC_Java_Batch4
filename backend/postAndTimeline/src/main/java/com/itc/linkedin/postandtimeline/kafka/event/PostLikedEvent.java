package com.itc.linkedin.postandtimeline.kafka.event;

import java.time.LocalDateTime;

public record PostLikedEvent(
        Long eventId,
        Long postId,
        String userId,
        int likesCount,
        LocalDateTime likedAt
) {
}
