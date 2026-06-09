package com.itc.linkedin.searchAndDiscover.dto;

public record JobSearchResponse(
        String id,
        String title,
        String companyName,
        String location,
        String workplaceType
) {
}
