package com.itc.linkedin.searchAndDiscover.dto;

public record PeopleSearchResponse(
        String id,
        String fullName,
        String headline,
        String location,
        String profileImageUrl,
        String connectionDegree
) {
}
