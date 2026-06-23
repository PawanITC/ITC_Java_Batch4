package com.itc.linkedin.api_gateway.controller;

import com.itc.linkedin.api_gateway.exception.GatewayErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/feed")
    public ResponseEntity<?> feedFallback() {
        return fallback("Feed and Timeline service is temporarily unavailable");
    }

    @RequestMapping("/search")
    public ResponseEntity<?> searchFallback() {
        return fallback("Search and Discovery service is temporarily unavailable");
    }

    @RequestMapping("/userprofile")
    public ResponseEntity<?> userProfileFallback() {
        return fallback("User Profile service is temporarily unavailable");
    }

    @GetMapping("/post")
    public Map<String, String> postFallback() {
        return Map.of(
                "service", "post-service",
                "message", "Post service is temporarily unavailable"
        );
    }

    private ResponseEntity<?> fallback(String message) {
        return ResponseEntity.status(503).body(
                Map.of(
                        "success", false,
                        "message", message,
                        "timestamp", LocalDateTime.now()
                )
        );
    }
    private ResponseEntity<GatewayErrorResponse> fallback(
            String message,
            String path
    ) {
        return ResponseEntity.status(503).body(
                new GatewayErrorResponse(
                        false,
                        message,
                        path,
                        LocalDateTime.now()
                )
        );
    }
}