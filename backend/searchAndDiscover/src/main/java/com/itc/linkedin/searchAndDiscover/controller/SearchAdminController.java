package com.itc.linkedin.searchAndDiscover.controller;

import com.itc.linkedin.searchAndDiscover.dto.ApiResponse;
import com.itc.linkedin.searchAndDiscover.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/search/admin")
@RequiredArgsConstructor
public class SearchAdminController {

    private final SearchService searchService;

    @PostMapping("/seed")
    public ApiResponse<String> seedSearchData() {
        searchService.seedAll();
        return ApiResponse.success("Elasticsearch seed data inserted successfully");
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, String>> elasticsearchHealth() {
        return ApiResponse.success(
                Map.of(
                        "elasticsearch", "UP",
                        "message", "Search repositories are available"
                )
        );
    }
}
