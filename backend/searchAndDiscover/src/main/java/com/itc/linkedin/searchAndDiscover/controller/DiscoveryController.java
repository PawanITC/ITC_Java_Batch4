package com.itc.linkedin.searchAndDiscover.controller;

import com.itc.linkedin.searchAndDiscover.dto.ApiResponse;
import com.itc.linkedin.searchAndDiscover.dto.CompanySearchResponse;
import com.itc.linkedin.searchAndDiscover.dto.DiscoverySuggestionResponse;
import com.itc.linkedin.searchAndDiscover.service.DiscoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discovery")
@RequiredArgsConstructor
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    @GetMapping("/suggestions")
    public ApiResponse<List<DiscoverySuggestionResponse>> getSuggestions(@RequestHeader(value = "X-User_Id", required = false) String userId) {
        return ApiResponse.success(discoveryService.getSuggestions(userId));
    }

    @GetMapping("/connections")
    public ApiResponse<List<DiscoverySuggestionResponse>> getConnectionSuggestions(@RequestHeader(value = "X-User_Id", required = false) String userId) {
        return ApiResponse.success(discoveryService.getConnectionSuggestions(userId));
    }
}
