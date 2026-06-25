package com.itc.linkedin.api_gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AllowedClientTokenValidatorTest {

    private final AllowedClientTokenValidator validator =
            new AllowedClientTokenValidator(List.of("linkedin-frontend", "api-gateway"));

    @Test
    void shouldAcceptTokenWhenAuthorizedPartyMatchesAllowedClient() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("azp", "linkedin-frontend")
                .build();

        assertTrue(validator.validate(jwt).hasErrors() == false);
    }

    @Test
    void shouldAcceptTokenWhenAudienceMatchesAllowedClient() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .audience(List.of("api-gateway"))
                .build();

        assertTrue(validator.validate(jwt).hasErrors() == false);
    }

    @Test
    void shouldRejectTokenWhenNoAllowedClientMatches() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("azp", "unknown-client")
                .audience(List.of("another-api"))
                .build();

        assertFalse(validator.validate(jwt).hasErrors() == false);
    }

    @Test
    void shouldRejectTokenWithoutAllowedClaimsInsteadOfThrowing() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("client_id", "unknown-client")
                .build();

        assertFalse(validator.validate(jwt).hasErrors() == false);
    }
}
