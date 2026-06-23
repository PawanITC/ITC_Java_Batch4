package com.itc.linkedin.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RelayUserHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return exchange.getPrincipal()
                .ofType(JwtAuthenticationToken.class)
                .flatMap(auth -> {
                    Jwt jwt = auth.getToken();

                    String username = jwt.getClaimAsString("preferred_username");
                    String email = jwt.getClaimAsString("email");
                    String userId = firstNonBlank(jwt.getSubject(), username, email);

                    log.info("Gateway authenticated userId={}, username={}", userId, username);

                    if (!StringUtils.hasText(userId)) {
                        log.warn("Rejecting request because JWT does not contain a usable user identifier");
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete().thenReturn(Boolean.TRUE);
                    }

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(builder -> builder.headers(headers -> {
                                headers.remove("X-User-Id");
                                headers.remove("X-Username");
                                headers.remove("X-Email");

                                headers.set("X-User-Id", userId);
                                headers.set("X-Username", username == null ? "" : username);
                                headers.set("X-Email", email == null ? "" : email);
                            }))
                            .build();

                    return chain.filter(mutatedExchange).thenReturn(Boolean.TRUE);
                })
                .switchIfEmpty(chain.filter(exchange).thenReturn(Boolean.TRUE))
                .then();
    }

    @Override
    public int getOrder() {
        return -50;
    }

    private String firstNonBlank(String... candidates) {
        for (String candidate : candidates) {
            if (StringUtils.hasText(candidate)) {
                return candidate;
            }
        }
        return null;
    }
}
