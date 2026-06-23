package com.itc.linkedin.api_gateway.filter;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RelayUserHeaderFilterTest {

    private final RelayUserHeaderFilter filter = new RelayUserHeaderFilter();

    @Test
    void shouldAddUserHeadersFromJwt() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("user-1")
                .claim("preferred_username", "user.demo")
                .claim("email", "user.demo@example.com")
                .build();

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);

        ServerWebExchange exchange = MockServerWebExchange.from(
                        MockServerHttpRequest.get("/api/feed")
                ).mutate()
                .principal(Mono.just(authentication))
                .build();

        AtomicReference<ServerWebExchange> capturedExchange =
                new AtomicReference<>();

        GatewayFilterChain chain = ex -> {
            capturedExchange.set(ex);
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertEquals("user-1",
                capturedExchange.get().getRequest().getHeaders().getFirst("X-User-Id"));

        assertEquals("user.demo",
                capturedExchange.get().getRequest().getHeaders().getFirst("X-Username"));

        assertEquals("user.demo@example.com",
                capturedExchange.get().getRequest().getHeaders().getFirst("X-Email"));
    }

    @Test
    void shouldContinueWithoutUserWhenNoPrincipal() {
        ServerWebExchange exchange = MockServerWebExchange.from(
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

        assertEquals(exchange, capturedExchange.get());
    }

    @Test
    void shouldFallbackToPreferredUsernameWhenSubjectMissing() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("preferred_username", "user.demo")
                .claim("email", "user.demo@example.com")
                .build();

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);

        ServerWebExchange exchange = MockServerWebExchange.from(
                        MockServerHttpRequest.get("/api/feed")
                ).mutate()
                .principal(Mono.just(authentication))
                .build();

        AtomicReference<ServerWebExchange> capturedExchange = new AtomicReference<>();

        GatewayFilterChain chain = ex -> {
            capturedExchange.set(ex);
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertEquals("user.demo",
                capturedExchange.get().getRequest().getHeaders().getFirst("X-User-Id"));
    }

    @Test
    void shouldReturnUnauthorizedWhenJwtHasNoUsableIdentity() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("preferred_username", "")
                .claim("email", "")
                .build();

        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);

        ServerWebExchange exchange = MockServerWebExchange.from(
                        MockServerHttpRequest.get("/api/feed")
                ).mutate()
                .principal(Mono.just(authentication))
                .build();

        GatewayFilterChain chain = ex -> Mono.error(new AssertionError("chain should not be called"));

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertEquals(401, exchange.getResponse().getStatusCode().value());
        assertNull(exchange.getResponse().getHeaders().getFirst("X-User-Id"));
    }
}
