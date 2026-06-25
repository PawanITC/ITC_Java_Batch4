package com.itc.linkedin.api_gateway.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

public class AllowedClientTokenValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error INVALID_TOKEN = new OAuth2Error(
            "invalid_token",
            "Token is not intended for an allowed client",
            null
    );

    private final List<String> allowedTokenClients;

    public AllowedClientTokenValidator(List<String> allowedTokenClients) {
        this.allowedTokenClients = allowedTokenClients;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        String authorizedParty = token.getClaimAsString("azp");
        String clientId = token.getClaimAsString("client_id");
        List<String> audience = token.getAudience() == null ? Collections.emptyList() : token.getAudience();

        boolean matched = allowedTokenClients.stream().anyMatch(allowed ->
                matches(authorizedParty, allowed)
                        || matches(clientId, allowed)
                        || audience.contains(allowed)
        );

        return matched
                ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(INVALID_TOKEN);
    }

    private boolean matches(String actual, String expected) {
        return StringUtils.hasText(actual) && actual.equals(expected);
    }
}
