package com.itc.linkedin.searchAndDiscover.service;

import com.itc.linkedin.searchAndDiscover.dto.TrendingTopicResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrendingService {

    public List<TrendingTopicResponse> getTrendingTopics() {
        return List.of(
                new TrendingTopicResponse("Spring Boot", 1500, "Technology"),
                new TrendingTopicResponse("Kubernetes", 980, "DevOps"),
                new TrendingTopicResponse("Keycloak", 650, "Security")
        );
    }
}
