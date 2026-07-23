package com.itc.linkedin.api_gateway.config;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter.Response;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayResilienceIntegrationTest {

    private static final AtomicInteger searchAttempts = new AtomicInteger();
    private static final AtomicInteger profileRequests = new AtomicInteger();
    private static final DisposableServer backendServer = HttpServer.create()
            .port(0)
            .route(routes -> routes
                    .get("/api/search/test", (request, response) -> {
                        int attempt = searchAttempts.incrementAndGet();
                        if (attempt < 3) {
                            response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                            return response.sendString(Mono.just("temporary failure"));
                        }

                        response.status(HttpResponseStatus.OK);
                        return response.sendString(Mono.just("search ok"));
                    })
                    .get("/api/profiles/me", (request, response) -> {
                        profileRequests.incrementAndGet();
                        response.status(HttpResponseStatus.OK);
                        response.header("Content-Type", "application/json");
                        return response.sendString(Mono.just("""
                                {"id":"11111111-1111-1111-1111-111111111111","keycloakUserId":"user-1","email":"user@example.com"}
                                """));
                    })
            )
            .bindNow();

    @LocalServerPort
    private int port;

    @MockitoBean
    private RedisRateLimiter redisRateLimiter;

    @MockitoBean
    private ReactiveJwtDecoder jwtDecoder;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("SEARCH_SERVICE_URI", () -> "http://localhost:" + backendServer.port());
        registry.add("USERPROFILE_SERVICE_URI", () -> "http://localhost:" + backendServer.port());
        registry.add("KEYCLOAK_JWK_SET_URI", () -> "http://localhost/fake-jwks");
    }

    @BeforeEach
    void setUp() {
        searchAttempts.set(0);
        profileRequests.set(0);
        when(redisRateLimiter.isAllowed(anyString(), anyString()))
                .thenReturn(Mono.just(new Response(true, Map.of("X-RateLimit-Remaining", "19"))));
        when(jwtDecoder.decode(anyString()))
                .thenReturn(Mono.just(
                        Jwt.withTokenValue("test-token")
                                .header("alg", "none")
                                .subject("user-1")
                                .claim("azp", "linkedin-frontend")
                                .claim("preferred_username", "user-1")
                                .build()
                ));
    }

    @AfterAll
    static void tearDown() {
        backendServer.disposeNow();
    }

    @Test
    void shouldReturnTooManyRequestsWhenRateLimiterRejectsCall() {
        when(redisRateLimiter.isAllowed(anyString(), anyString()))
                .thenReturn(Mono.just(new Response(false, Map.of())));

        testClient()
                .get()
                .uri("/api/search/test")
                .header("Authorization", "Bearer test-token")
                .exchange()
                .expectStatus().isEqualTo(429);

        assertEquals(0, searchAttempts.get());
    }

    @Test
    void shouldRetryGatewayCallUntilBackendSucceeds() {
        testClient()
                .get()
                .uri("/api/search/test")
                .header("Authorization", "Bearer test-token")
                .exchange()
                .expectStatus().isOk();

        assertEquals(3, searchAttempts.get());
    }

    @Test
    void shouldForwardCurrentProfileRequestToUserProfileService() {
        testClient()
                .get()
                .uri("/api/profiles/me")
                .header("Authorization", "Bearer test-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.keycloakUserId").isEqualTo("user-1")
                .jsonPath("$.email").isEqualTo("user@example.com");

        assertEquals(1, profileRequests.get());
    }

    @Test
    void shouldExposePrometheusEndpointForClusterScraping() {
        testClient()
                .get()
                .uri("/actuator/prometheus")
                .exchange()
                .expectStatus().isOk();
    }

    private WebTestClient testClient() {
        return WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }
}
