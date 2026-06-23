package com.itc.linkedin.postandtimeline.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponse(
        Long id,
        String authorId,
        String authorName,
        String authorHeadline,
        String content,
        int likesCount,
        int commentsCount,
        LocalDateTime createdAt
) {
}