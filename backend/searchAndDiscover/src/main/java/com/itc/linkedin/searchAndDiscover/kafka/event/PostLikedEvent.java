package com.itc.linkedin.searchAndDiscover.kafka.event;

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
}
