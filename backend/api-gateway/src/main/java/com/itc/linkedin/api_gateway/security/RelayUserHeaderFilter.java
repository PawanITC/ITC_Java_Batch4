package com.itc.linkedin.api_gateway.security;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

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
                            String username = jwt.getClaimAsString("username");

                            List<String> roles = jwt.getClaimAsStringList("roles");
                            if (roles == null && jwt.getClaim("realm_access") != null) {
                                Object realm_access = jwt.getClaim("realm_access");
                                roles = List.of(realm_access.toString());
                            }

                            List<String> finalRoles = roles;

                            return exchange.mutate()
                                    .request(request -> request.headers(headers -> {
                                        headers.add("X-User_Id", userId);

                                        if (email != null) {
                                            headers.add("X-User-Email", email);
                                        }

                                        if (username != null) {
                                            headers.add("X-Username", username);
                                        }

                                        if (finalRoles != null) {
                                            headers.add("X-User-Roles", String.join(",", finalRoles));
                                        }
                                    }))
                                    .build();

                        })
                        .defaultIfEmpty(exchange)
                        .flatMap(chain::filter);
    }
}
