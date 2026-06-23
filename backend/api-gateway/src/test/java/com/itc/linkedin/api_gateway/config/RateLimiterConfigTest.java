package com.itc.linkedin.api_gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class RateLimiterConfigTest {

    private final RateLimiterConfig config = new RateLimiterConfig();

    @Test
    void shouldCreateUserKeyResolver() {
        KeyResolver resolver = config.userKeyResolver();

        assertNotNull(resolver);
    }
}