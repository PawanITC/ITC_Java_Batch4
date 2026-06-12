package com.itc.linkedin.searchAndDiscover.dto;

public record TrendingTopicResponse(
        String topic,
        int postCount,
        String category
) {
}
