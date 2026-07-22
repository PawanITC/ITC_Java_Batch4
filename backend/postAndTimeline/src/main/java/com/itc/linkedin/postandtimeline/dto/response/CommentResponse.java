package com.itc.linkedin.postandtimeline.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponse(
        Long id,
        Long postId,
        String authorId,
        String authorName,
        String content,
        LocalDateTime createdAt
) {
}
