package com.itc.linkedin.apigateway.fallback;

import com.itc.linkedin.api_gateway.controller.FallbackController;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class FallbackControllerTest {

    private final FallbackController controller = new FallbackController();

    @Test
    void shouldReturnFeedFallback() {
        ResponseEntity<?> response = controller.feedFallback();

        assertEquals(503, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldReturnSearchFallback() {
        ResponseEntity<?> response = controller.searchFallback();

        assertEquals(503, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldReturnUserProfileFallback() {
        ResponseEntity<?> response = controller.userProfileFallback();

        assertEquals(503, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }
}