package com.itc.linkedin.searchAndDiscover.controller;

import com.itc.linkedin.searchAndDiscover.dto.ApiResponse;
import com.itc.linkedin.searchAndDiscover.dto.TrendingTopicResponse;
import com.itc.linkedin.searchAndDiscover.service.TrendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/discovery/trending")
@RequiredArgsConstructor
public class TrendingController {

    private final TrendingService trendingService;

    @GetMapping("/topics")
    public ApiResponse<List<TrendingTopicResponse>> getTrendingTopic() {
        return ApiResponse.success(trendingService.getTrendingTopics());
    }
}
