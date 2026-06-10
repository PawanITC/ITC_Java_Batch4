package com.itc.linkedin.api_gateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityConfigTest {

    @LocalServerPort
    int port;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void actuatorHealthShouldWork() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void gatewayHealthShouldWork() {
        webTestClient.get()
                .uri("/gateway/health")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void searchApiWithoutTokenShouldReturnUnauthorized() {
        webTestClient.get()
                .uri("/api/search/people?q=java")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void discoveryApiWithoutTokenShouldReturnUnauthorized() {
        webTestClient.get()
                .uri("/api/discovery/suggestions")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void fallbackEndpointShouldReturnFallbackResponse() {
        webTestClient.get()
                .uri("/fallback/search-discovery")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("fallback")
                .jsonPath("$.message").isEqualTo("Search and Discovery service is temporarily unavailable");
    }
}