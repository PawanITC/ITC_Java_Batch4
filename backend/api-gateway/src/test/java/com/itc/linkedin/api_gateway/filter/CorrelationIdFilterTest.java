package com.itc.linkedin.api_gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter =
            new CorrelationIdFilter();

    @Test
    void shouldAddCorrelationIdWhenMissing() {
        MockServerWebExchange exchange =
                MockServerWebExchange.from(
                        MockServerHttpRequest.get("/api/feed")
                );

        AtomicReference<ServerWebExchange> capturedExchange =
                new AtomicReference<>();

        GatewayFilterChain chain = ex -> {
            capturedExchange.set(ex);
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        String correlationId = capturedExchange.get()
                .getRequest()
                .getHeaders()
                .getFirst("X-Correlation-Id");

        assertNotNull(correlationId);
        assertFalse(correlationId.isBlank());
    }

    @Test
    void shouldKeepExistingCorrelationId() {
        MockServerWebExchange exchange =
                MockServerWebExchange.from(
                        MockServerHttpRequest.get("/api/feed")
                                .header("X-Correlation-Id", "test-id-123")
                );

        AtomicReference<ServerWebExchange> capturedExchange =
                new AtomicReference<>();

        GatewayFilterChain chain = ex -> {
            capturedExchange.set(ex);
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertEquals(
                "test-id-123",
                capturedExchange.get()
                        .getRequest()
                        .getHeaders()
                        .getFirst("X-Correlation-Id")
        );
    }
}