package com.itc.linkedin.searchAndDiscover.dto;

public record CompanySearchResponse(
        String id,
        String name,
        String industry,
        String location,
        int followers
) {
}
