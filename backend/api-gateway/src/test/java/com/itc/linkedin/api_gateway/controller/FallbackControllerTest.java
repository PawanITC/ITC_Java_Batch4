package com.itc.linkedin.api_gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

class FallbackControllerTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient
                .bindToController(new FallbackController())
                .build();
    }

    @Test
    void searchDiscoveryFallbackShouldReturnFallbackResponse() {
        webTestClient.get()
                .uri("/fallback/search-discovery")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("fallback")
                .jsonPath("$.message")
                .isEqualTo("Search and Discovery service is temporarily unavailable");
    }

    @Test
    void profileFallbackShouldReturnFallbackResponse() {
        webTestClient.get()
                .uri("/fallback/profile")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("fallback")
                .jsonPath("$.message")
                .isEqualTo("Profile service is temporarily unavailable");
    }

    @Test
    void postsFallbackShouldReturnFallbackResponse() {
        webTestClient.get()
                .uri("/fallback/posts")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("fallback")
                .jsonPath("$.message")
                .isEqualTo("Post service is temporarily unavailable");
    }
}