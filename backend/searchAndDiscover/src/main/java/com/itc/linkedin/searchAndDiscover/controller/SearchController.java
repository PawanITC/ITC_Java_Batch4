package com.itc.linkedin.searchAndDiscover.controller;

import com.itc.linkedin.searchAndDiscover.dto.*;
import com.itc.linkedin.searchAndDiscover.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/people")
    public ApiResponse<List<PeopleSearchResponse>> searchPeople(@RequestParam String q) {
        return ApiResponse.success(searchService.searchPeople(q));
    }

    @GetMapping("/posts")
    public ApiResponse<List<PostSearchResponse>> searchPosts(@RequestParam String q) {
        return ApiResponse.success(searchService.searchPosts(q));
    }

    @GetMapping("/jobs")
    public ApiResponse<List<JobSearchResponse>> searchJobs(@RequestParam String q) {
        return ApiResponse.success(searchService.searchJobs(q));
    }

    @GetMapping("/companies")
    public ApiResponse<List<CompanySearchResponse>> searchCompanies(@RequestParam String q) {
        return ApiResponse.success(searchService.searchCompanies(q));
    }
}
