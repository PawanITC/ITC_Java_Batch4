package com.itc.linkedin.searchAndDiscover.controller;

import com.itc.linkedin.searchAndDiscover.dto.*;
import com.itc.linkedin.searchAndDiscover.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/seed")
    public ApiResponse<String> seedAll() {
        searchService.seedAll();
        return ApiResponse.success("Search indexes seeded successfully");
    }

    @GetMapping("/people")
    public ApiResponse<List<PeopleSearchResponse>> searchPeople(
            @RequestParam String q,
            @RequestHeader("X-User-Id") String userId
    ) {

        return ApiResponse.success(searchService.searchPeople(q, userId));
    }

    @GetMapping("/posts")
    public ApiResponse<List<PostSearchResponse>> searchPosts(
            @RequestParam String q,
            @RequestHeader("X-User-Id") String userId
    ) {

        return ApiResponse.success(searchService.searchPosts(q, userId));
    }

    @GetMapping("/jobs")
    public ApiResponse<List<JobSearchResponse>> searchJobs(
            @RequestParam String q,
            @RequestHeader("X-User-Id") String userId
    ) {

        return ApiResponse.success(searchService.searchJobs(q, userId));
    }

    @GetMapping("/companies")
    public ApiResponse<List<CompanySearchResponse>> searchCompanies(
            @RequestParam String q,
            @RequestHeader("X-User-Id") String userId
    ) {

        return ApiResponse.success(searchService.searchCompanies(q, userId));
    }
}