package com.itc.linkedin.api_gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

class GatewayHealthControllerTest {

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        webTestClient = WebTestClient
                .bindToController(new GatewayHealthController())
                .build();
    }

    @Test
    void healthShouldReturnGatewayRunningMessage() {
        webTestClient.get()
                .uri("/gateway/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("API Gateway is running");
    }
}