package com.itc.linkedin.api_gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GatewayRouteConfigTest {

    @Autowired
    private GatewayProperties gatewayProperties;

    @Test
    void shouldConfigureDefaultRateLimiterAndRetryFilters() {
        List<String> filterNames = gatewayProperties.getDefaultFilters().stream()
                .map(filter -> filter.getName())
                .toList();

        assertTrue(filterNames.contains("RequestRateLimiter"));
        assertTrue(filterNames.contains("Retry"));
    }

    @Test
    void shouldKeepConfiguredGatewayRoutes() {
        List<RouteDefinition> routes = gatewayProperties.getRoutes();

        assertEquals(9, routes.size());
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("userprofile-service")));
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("search-discovery-service")));
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("trending-service")));
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("feed-and-timeline-service")));
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("post-and-timeline-service")));
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("jobposting-service")));
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("notification-service-root")));
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("notification-service")));
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("connections-service")));
    }

    @Test
    void shouldRouteUserProfileSectionEndpoints() {
        RouteDefinition userProfileRoute = gatewayProperties.getRoutes().stream()
                .filter(route -> route.getId().equals("userprofile-service"))
                .findFirst()
                .orElseThrow();

        String pathPredicate = userProfileRoute.getPredicates().stream()
                .filter(predicate -> predicate.getName().equals("Path"))
                .findFirst()
                .orElseThrow()
                .getArgs()
                .values()
                .toString();

        assertTrue(pathPredicate.contains("/api/profiles/**"));
        assertTrue(pathPredicate.contains("/api/skills"));
        assertTrue(pathPredicate.contains("/api/skills/**"));
        assertTrue(pathPredicate.contains("/api/educations"));
        assertTrue(pathPredicate.contains("/api/educations/**"));
        assertTrue(pathPredicate.contains("/api/experiences"));
        assertTrue(pathPredicate.contains("/api/experiences/**"));
        assertTrue(pathPredicate.contains("/api/languages"));
        assertTrue(pathPredicate.contains("/api/languages/**"));
        assertTrue(pathPredicate.contains("/api/follows"));
        assertTrue(pathPredicate.contains("/api/follows/**"));
    }
}
