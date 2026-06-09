package com.itc.linkedin.searchAndDiscover.service;

import com.itc.linkedin.searchAndDiscover.dto.DiscoverySuggestionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscoveryService {

    public List<DiscoverySuggestionResponse> getSuggestions(String userId) {
        return List.of(
                new DiscoverySuggestionResponse(
                        "user-3",
                        "Ananya Sharma",
                        "Spring Boot Developer",
                        "Because you both know Java and Spring Boot",
                        null
                ),
                new DiscoverySuggestionResponse(
                        "user-4",
                        "James Wilson",
                        "Cloud Engineer",
                        "Because you are interested in Kubernetes",
                        null
                )
        );
    }

    public List<DiscoverySuggestionResponse> getConnectionSuggestions(String userId) {
        return getSuggestions(userId);
    }
}
