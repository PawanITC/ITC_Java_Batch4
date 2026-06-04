package com.itc.linkedin.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/search")
    public Mono<Map<String, String>> searchFallback() {
        return Mono.just(Map.of(
                "status", "fallback",
                "message", "Search and discovery service is temporarily unavailable"
        ));
    }
}
