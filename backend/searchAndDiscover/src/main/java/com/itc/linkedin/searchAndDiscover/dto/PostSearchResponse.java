package com.itc.linkedin.searchAndDiscover.dto;

public record PostSearchResponse(
        String id,
        String authorName,
        String content,
        int likes,
        int comments
) {
}
