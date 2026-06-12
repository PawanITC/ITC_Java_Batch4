package com.itc.linkedin.api_gateway.security;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Map;

@Configuration
public class RelayUserHeaderFilter {

    @Bean
    public GlobalFilter relayUserHeaders() {
        return (exchange, chain) ->
                exchange.getPrincipal()
                        .cast(JwtAuthenticationToken.class)
                        .map(authentication -> {
                            Jwt jwt = authentication.getToken();

                            String userId = jwt.getSubject();
                            String email = jwt.getClaimAsString("email");
                            String username = jwt.getClaimAsString("preferred_username");

                            Map<String, Object> realmAccess = jwt.getClaim("realm_access");

                            List<String> roles = List.of();

                            if (realmAccess != null && realmAccess.get("roles") instanceof List<?> roleList) {
                                roles = roleList.stream()
                                        .map(Object::toString)
                                        .toList();
                            }

                            List<String> finalRoles = roles;

                            return exchange.mutate()
                                    .request(request -> request.headers(headers -> {
                                        headers.set("X-User-Id", userId);

                                        if (email != null) {
                                            headers.set("X-User-Email", email);
                                        }

                                        if (username != null) {
                                            headers.set("X-Username", username);
                                        }

                                        headers.set("X-User-Roles", String.join(",", finalRoles));
                                    }))
                                    .build();
                        })
                        .defaultIfEmpty(exchange)
                        .flatMap(chain::filter);
    }
}