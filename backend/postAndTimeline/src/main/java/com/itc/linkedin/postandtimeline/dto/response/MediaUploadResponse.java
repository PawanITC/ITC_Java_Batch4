package com.itc.linkedin.postandtimeline.dto.response;

public record MediaUploadResponse(
        String mediaUrl,
        String mediaType,
        String objectKey,
        String mediaObjectKey
) {
}
