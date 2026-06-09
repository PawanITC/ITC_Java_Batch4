package com.itc.linkedin.searchAndDiscover.dto;

public record DiscoverySuggestionResponse(
        String id,
        String fullName,
        String headline,
        String reason,
        String profileImageUrl
) {
}
