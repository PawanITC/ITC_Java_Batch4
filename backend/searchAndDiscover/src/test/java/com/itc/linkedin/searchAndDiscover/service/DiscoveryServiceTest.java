package com.itc.linkedin.searchAndDiscover.service;

import com.itc.linkedin.searchAndDiscover.dto.DiscoverySuggestionResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiscoveryServiceTest {

    private final DiscoveryService discoveryService =
            new DiscoveryService();

    @Test
    void shouldReturnSuggestions() {

        List<DiscoverySuggestionResponse> suggestions =
                discoveryService.getSuggestions("user-1");

        assertNotNull(suggestions);
        assertEquals(2, suggestions.size());

        assertEquals(
                "Ananya Sharma",
                suggestions.get(0).fullName()
        );
    }

    @Test
    void shouldReturnConnectionSuggestions() {

        List<DiscoverySuggestionResponse> suggestions =
                discoveryService.getConnectionSuggestions("user-1");

        assertNotNull(suggestions);
        assertEquals(2, suggestions.size());
    }
}