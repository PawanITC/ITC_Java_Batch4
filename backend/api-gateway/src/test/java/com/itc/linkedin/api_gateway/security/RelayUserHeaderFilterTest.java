package com.itc.linkedin.api_gateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RelayUserHeaderFilterTest {

    private final RelayUserHeaderFilter relayUserHeaderFilter =
            new RelayUserHeaderFilter();

    @Test
    void shouldAddUserHeadersFromJwt() {
        GlobalFilter filter = relayUserHeaderFilter.relayUserHeaders();

        Jwt jwt = createJwtWithAllClaims();

        JwtAuthenticationToken authentication =
                new JwtAuthenticationToken(jwt);

        ServerWebExchange exchange =
                createExchangeWithPrincipal(authentication);

        StepVerifier.create(
                filter.filter(exchange, filteredExchange -> {
                    HttpHeaders headers =
                            filteredExchange.getRequest().getHeaders();

                    assertThat(headers.getFirst("X-User-Id"))
                            .isEqualTo("user-123");

                    assertThat(headers.getFirst("X-User-Email"))
                            .isEqualTo("shubhra@test.com");

                    assertThat(headers.getFirst("X-Username"))
                            .isEqualTo("shubhra");

                    assertThat(headers.getFirst("X-User-Roles"))
                            .contains("USER")
                            .contains("RECRUITER");

                    return Mono.empty();
                })
        ).verifyComplete();
    }

    @Test
    void shouldAddUserIdEvenWhenEmailAndUsernameAreMissing() {
        GlobalFilter filter = relayUserHeaderFilter.relayUserHeaders();

        Jwt jwt = new Jwt(
                "fake-token",
                Instant.now(),
                Instant.now().plusSeconds(600),
                Map.of("alg", "none"),
                Map.of(
                        "sub", "user-456",
                        "realm_access", Map.of("roles", List.of("USER"))
                )
        );

        JwtAuthenticationToken authentication =
                new JwtAuthenticationToken(jwt);

        ServerWebExchange exchange =
                createExchangeWithPrincipal(authentication);

        StepVerifier.create(
                filter.filter(exchange, filteredExchange -> {
                    HttpHeaders headers =
                            filteredExchange.getRequest().getHeaders();

                    assertThat(headers.getFirst("X-User-Id"))
                            .isEqualTo("user-456");

                    assertThat(headers.getFirst("X-User-Email"))
                            .isNull();

                    assertThat(headers.getFirst("X-Username"))
                            .isNull();

                    assertThat(headers.getFirst("X-User-Roles"))
                            .contains("USER");

                    return Mono.empty();
                })
        ).verifyComplete();
    }

    @Test
    void shouldSetEmptyRolesHeaderWhenRealmAccessIsMissing() {
        GlobalFilter filter = relayUserHeaderFilter.relayUserHeaders();

        Jwt jwt = new Jwt(
                "fake-token",
                Instant.now(),
                Instant.now().plusSeconds(600),
                Map.of("alg", "none"),
                Map.of(
                        "sub", "user-789",
                        "email", "no-role@test.com",
                        "preferred_username", "norole"
                )
        );

        JwtAuthenticationToken authentication =
                new JwtAuthenticationToken(jwt);

        ServerWebExchange exchange =
                createExchangeWithPrincipal(authentication);

        StepVerifier.create(
                filter.filter(exchange, filteredExchange -> {
                    HttpHeaders headers =
                            filteredExchange.getRequest().getHeaders();

                    assertThat(headers.getFirst("X-User-Id"))
                            .isEqualTo("user-789");

                    assertThat(headers.getFirst("X-User-Email"))
                            .isEqualTo("no-role@test.com");

                    assertThat(headers.getFirst("X-Username"))
                            .isEqualTo("norole");

                    assertThat(headers.getFirst("X-User-Roles"))
                            .isEmpty();

                    return Mono.empty();
                })
        ).verifyComplete();
    }

    @Test
    void shouldContinueRequestWhenPrincipalIsMissing() {
        GlobalFilter filter = relayUserHeaderFilter.relayUserHeaders();

        MockServerHttpRequest request =
                MockServerHttpRequest.get("/api/search/people?q=java")
                        .build();

        ServerWebExchange exchange =
                MockServerWebExchange.from(request);

        StepVerifier.create(
                filter.filter(exchange, filteredExchange -> {
                    HttpHeaders headers =
                            filteredExchange.getRequest().getHeaders();

                    assertThat(headers.getFirst("X-User-Id")).isNull();
                    assertThat(headers.getFirst("X-User-Email")).isNull();
                    assertThat(headers.getFirst("X-Username")).isNull();
                    assertThat(headers.getFirst("X-User-Roles")).isNull();

                    return Mono.empty();
                })
        ).verifyComplete();
    }

    private Jwt createJwtWithAllClaims() {
        return new Jwt(
                "fake-token",
                Instant.now(),
                Instant.now().plusSeconds(600),
                Map.of("alg", "none"),
                Map.of(
                        "sub", "user-123",
                        "email", "shubhra@test.com",
                        "preferred_username", "shubhra",
                        "realm_access", Map.of(
                                "roles", List.of("USER", "RECRUITER")
                        )
                )
        );
    }

    private ServerWebExchange createExchangeWithPrincipal(
            JwtAuthenticationToken authentication
    ) {
        MockServerHttpRequest request =
                MockServerHttpRequest.get("/api/search/people?q=java")
                        .build();

        return MockServerWebExchange.from(request)
                .mutate()
                .principal(Mono.just(authentication))
                .build();
    }

    @Test
    void shouldHandleRealmAccessWithoutRoles() {

        GlobalFilter filter =
                relayUserHeaderFilter.relayUserHeaders();

        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(600),
                Map.of("alg","none"),
                Map.of(
                        "sub","user-999",
                        "realm_access", Map.of()
                )
        );

        JwtAuthenticationToken auth =
                new JwtAuthenticationToken(jwt);

        ServerWebExchange exchange =
                createExchangeWithPrincipal(auth);

        StepVerifier.create(
                filter.filter(exchange, ex -> {

                    HttpHeaders headers =
                            ex.getRequest().getHeaders();

                    assertThat(
                            headers.getFirst("X-User-Roles")
                    ).isEmpty();

                    return Mono.empty();
                })
        ).verifyComplete();
    }
}