package com.itc.linkedin.postandtimeline.dto.request;

import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @Size(max = 5000)
        String content,
        String mediaObjectKey,
        String mediaType
) {
}
