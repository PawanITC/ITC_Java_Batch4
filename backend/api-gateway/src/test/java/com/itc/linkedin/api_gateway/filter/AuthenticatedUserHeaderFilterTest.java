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

import java.security.Principal;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticatedUserHeaderFilterTest {

    private final AuthenticatedUserHeaderFilter filter = new AuthenticatedUserHeaderFilter();

    @Test
    void shouldUsePreferredUsernameHeaderWhenSubjectIsMissing() {
        MockServerWebExchange exchange = MockServerWebExchange.builder(
                MockServerHttpRequest.get("/api/profiles/me")
                        .header(AuthenticatedUserHeaderFilter.USER_ID_HEADER, "spoofed-user")
        ).principal(authenticationWithoutSubject()).build();

        AtomicReference<ServerWebExchange> capturedExchange = new AtomicReference<>();
        GatewayFilterChain chain = ex -> {
            capturedExchange.set(ex);
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assertEquals(
                "user.demo",
                capturedExchange.get().getRequest().getHeaders()
                        .getFirst(AuthenticatedUserHeaderFilter.USER_ID_HEADER)
        );
        assertEquals(
                "user.demo",
                capturedExchange.get().getRequest().getHeaders()
                        .getFirst(AuthenticatedUserHeaderFilter.USERNAME_HEADER)
        );
        assertEquals(
                "user.demo@example.com",
                capturedExchange.get().getRequest().getHeaders()
                        .getFirst(AuthenticatedUserHeaderFilter.EMAIL_HEADER)
        );
    }

    private Principal authenticationWithoutSubject() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("preferred_username", "user.demo")
                .claim("email", "user.demo@example.com")
                .build();

        return new JwtAuthenticationToken(jwt);
    }
}
