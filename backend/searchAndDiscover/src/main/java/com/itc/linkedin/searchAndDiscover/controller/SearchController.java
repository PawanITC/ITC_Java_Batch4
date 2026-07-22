package com.itc.linkedin.searchAndDiscover.controller;

import com.itc.linkedin.searchAndDiscover.dto.*;
import com.itc.linkedin.searchAndDiscover.security.CurrentUserService;
import com.itc.linkedin.searchAndDiscover.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final CurrentUserService currentUserService;

    @PostMapping("/seed")
    public ApiResponse<String> seedAll() {
        searchService.seedAll();
        return ApiResponse.success("Search indexes seeded successfully");
    }

    @GetMapping("/people")
    public ApiResponse<List<PeopleSearchResponse>> searchPeople(
            @RequestParam String q,
            Authentication authentication
    ) {
        String userId = requiredUserId(authentication);
        return ApiResponse.success(searchService.searchPeople(q, userId));
    }

    @GetMapping("/posts")
    public ApiResponse<List<PostSearchResponse>> searchPosts(
            @RequestParam String q,
            Authentication authentication
    ) {
        String userId = requiredUserId(authentication);
        return ApiResponse.success(searchService.searchPosts(q, userId));
    }

    @GetMapping("/jobs")
    public ApiResponse<List<JobSearchResponse>> searchJobs(
            @RequestParam String q,
            Authentication authentication
    ) {
        String userId = requiredUserId(authentication);
        return ApiResponse.success(searchService.searchJobs(q, userId));
    }

    @GetMapping("/companies")
    public ApiResponse<List<CompanySearchResponse>> searchCompanies(
            @RequestParam String q,
            Authentication authentication
    ) {
        String userId = requiredUserId(authentication);
        return ApiResponse.success(searchService.searchCompanies(q, userId));
    }

    private String requiredUserId(Authentication authentication) {
        String userId = currentUserService.getUserId(authentication);
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Missing user identity in JWT");
        }
        return userId;
    }
}
