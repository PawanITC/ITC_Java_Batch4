package com.itc.linkedin.feedAndTimeline.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TimelinePostResponse(
        Long postId,
        String authorId,
        String authorName,
        String authorHeadline,
        String authorAvatarUrl,
        String content,
        String mediaUrl,
        String mediaObjectKey,
        String mediaType,
        int likesCount,
        int commentsCount,
        LocalDateTime createdAt
) {
}
