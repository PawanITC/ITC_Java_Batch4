package com.itc.linkedin.searchAndDiscover.service;

import com.itc.linkedin.searchAndDiscover.dto.TrendingTopicResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrendingServiceTest {

    private final TrendingService trendingService =
            new TrendingService();

    @Test
    void shouldReturnTrendingTopics() {

        List<TrendingTopicResponse> topics =
                trendingService.getTrendingTopics("user-1");

        assertNotNull(topics);
        assertEquals(3, topics.size());
    }

    @Test
    void shouldContainSpringBootTopic() {

        List<TrendingTopicResponse> topics =
                trendingService.getTrendingTopics("user-1");

        TrendingTopicResponse topic = topics.get(0);

        // If TrendingTopicResponse is a record
        assertEquals("Spring Boot", topic.topic());
        assertEquals(1500, topic.postCount());
        assertEquals("Technology", topic.category());
    }
}