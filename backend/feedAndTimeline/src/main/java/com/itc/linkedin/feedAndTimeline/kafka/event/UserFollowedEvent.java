package com.itc.linkedin.feedAndTimeline.kafka.event;

public record UserFollowedEvent(
        String eventId,
        String followerId,
        String followingId,
        String followerFirstName,
        String followerLastName,
        String followerEmail,
        String followingFirstName,
        String followingLastName,
        String followingEmail,
        String timestamp
) {
}
