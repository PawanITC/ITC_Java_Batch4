package com.itc.linkedin.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/search-discovery")
    public Map<String, String> searchDiscoveryFallback() {
        return Map.of(
                "status", "fallback",
                "message", "Search and Discovery service is temporarily unavailable"
        );
    }

    @GetMapping("/fallback/profile")
    public Map<String, String> profileFallback() {
        return Map.of(
                "status", "fallback",
                "message", "Profile service is temporarily unavailable"
        );
    }

    @GetMapping("/fallback/posts")
    public Map<String, String> postsFallback() {
        return Map.of(
                "status", "fallback",
                "message", "Post service is temporarily unavailable"
        );
    }
}