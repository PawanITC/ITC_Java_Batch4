package com.itc.linkedin.searchAndDiscover.controller;

import com.itc.linkedin.searchAndDiscover.dto.ApiResponse;
import com.itc.linkedin.searchAndDiscover.dto.DiscoverySuggestionResponse;
import com.itc.linkedin.searchAndDiscover.security.CurrentUserService;
import com.itc.linkedin.searchAndDiscover.service.DiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/discovery")
@RequiredArgsConstructor
public class DiscoveryController {

    private final DiscoveryService discoveryService;
    private final CurrentUserService currentUserService;

    @GetMapping("/suggestions")
    public ApiResponse<List<DiscoverySuggestionResponse>> getSuggestions(
            Authentication authentication
    ) {
        String userId = requiredUserId(authentication);
        return ApiResponse.success(
                discoveryService.getSuggestions(userId)
        );
    }

    @GetMapping("/connections")
    public ApiResponse<List<DiscoverySuggestionResponse>> getConnectionSuggestions(
            Authentication authentication
    ) {
        String userId = requiredUserId(authentication);
        return ApiResponse.success(
                discoveryService.getConnectionSuggestions(userId)
        );
    }

    private String requiredUserId(Authentication authentication) {
        String userId = currentUserService.getUserId(authentication);
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Missing user identity in JWT");
        }
        return userId;
    }
}
