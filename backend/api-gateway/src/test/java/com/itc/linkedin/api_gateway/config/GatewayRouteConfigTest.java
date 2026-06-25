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

        assertEquals(5, routes.size());
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("userprofile-service")));
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("search-discovery-service")));
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("trending-service")));
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("feed-and-timeline-service")));
        assertTrue(routes.stream().anyMatch(route -> route.getId().equals("post-and-timeline-service")));
    }
}
