package com.itc.linkedin.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticatedUserHeaderFilter implements GlobalFilter, Ordered {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USERNAME_HEADER = "X-Username";
    public static final String EMAIL_HEADER = "X-Email";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .map(jwt -> mutateWithUserHeaders(exchange, jwt))
                .defaultIfEmpty(stripUserHeaders(exchange))
                .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 20;
    }

    private ServerWebExchange mutateWithUserHeaders(ServerWebExchange exchange, Jwt jwt) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(USER_ID_HEADER);
                    headers.remove(USERNAME_HEADER);
                    headers.remove(EMAIL_HEADER);
                    headers.set(USER_ID_HEADER, jwt.getSubject());

                    String username = jwt.getClaimAsString("preferred_username");
                    if (username != null && !username.isBlank()) {
                        headers.set(USERNAME_HEADER, username);
                    }

                    String email = jwt.getClaimAsString("email");
                    if (email != null && !email.isBlank()) {
                        headers.set(EMAIL_HEADER, email);
                    }
                })
                .build();

        return exchange.mutate().request(request).build();
    }

    private ServerWebExchange stripUserHeaders(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(USER_ID_HEADER);
                    headers.remove(USERNAME_HEADER);
                    headers.remove(EMAIL_HEADER);
                })
                .build();

        return exchange.mutate().request(request).build();
    }
}
