package com.itc.linkedin.api_gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RateLimiterConfigTest {

    private final RateLimiterConfig config = new RateLimiterConfig();

    @Test
    void shouldCreateUserKeyResolver() {
        KeyResolver resolver = config.userKeyResolver();

        assertNotNull(resolver);
    }

    @Test
    void shouldResolveJwtSubjectAsRateLimitKey() {
        KeyResolver resolver = config.userKeyResolver();
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                Jwt.withTokenValue("token")
                        .header("alg", "none")
                        .subject("user-123")
                        .build()
        );

        when(exchange.getPrincipal()).thenReturn(reactor.core.publisher.Mono.just(authentication));

        StepVerifier.create(resolver.resolve(exchange))
                .expectNext("user-123")
                .verifyComplete();
    }

    @Test
    void shouldFallbackToPreferredUsernameWhenSubjectIsMissing() {
        KeyResolver resolver = config.userKeyResolver();
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                Jwt.withTokenValue("token")
                        .header("alg", "none")
                        .claim("preferred_username", "user.demo")
                        .build()
        );

        when(exchange.getPrincipal()).thenReturn(reactor.core.publisher.Mono.just(authentication));

        StepVerifier.create(resolver.resolve(exchange))
                .expectNext("user.demo")
                .verifyComplete();
    }

    @Test
    void shouldFallbackToAnonymousWhenPrincipalIsMissing() {
        KeyResolver resolver = config.userKeyResolver();
        ServerWebExchange exchange = mock(ServerWebExchange.class);

        when(exchange.getPrincipal()).thenReturn(reactor.core.publisher.Mono.empty());

        StepVerifier.create(resolver.resolve(exchange))
                .expectNext("anonymous")
                .verifyComplete();
    }
}
