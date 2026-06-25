package com.itc.linkedin.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> Mono.justOrEmpty(resolveKey(authentication)))
                .switchIfEmpty(Mono.just("anonymous"));
    }

    private String resolveKey(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt token = jwtAuthenticationToken.getToken();
            String subject = token.getSubject();
            if (StringUtils.hasText(subject)) {
                return subject;
            }

            String preferredUsername = token.getClaimAsString("preferred_username");
            if (StringUtils.hasText(preferredUsername)) {
                return preferredUsername;
            }

            String authorizedParty = token.getClaimAsString("azp");
            if (StringUtils.hasText(authorizedParty)) {
                return authorizedParty;
            }
        }

        String principalName = authentication.getName();
        return StringUtils.hasText(principalName) ? principalName : null;
    }
}
