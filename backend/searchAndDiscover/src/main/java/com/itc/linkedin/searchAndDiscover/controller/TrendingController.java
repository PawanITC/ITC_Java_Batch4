package com.itc.linkedin.searchAndDiscover.controller;

import com.itc.linkedin.searchAndDiscover.dto.ApiResponse;
import com.itc.linkedin.searchAndDiscover.dto.TrendingTopicResponse;
import com.itc.linkedin.searchAndDiscover.security.CurrentUserService;
import com.itc.linkedin.searchAndDiscover.service.TrendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/discovery/trending")
@RequiredArgsConstructor
public class TrendingController {

    private final TrendingService trendingService;
    private final CurrentUserService currentUserService;

    @GetMapping("/topics")
    public ApiResponse<List<TrendingTopicResponse>> getTrendingTopic(
            Authentication authentication
    ) {
        String userId = currentUserService.getUserId(authentication);
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Missing user identity in JWT");
        }
        return ApiResponse.success(trendingService.getTrendingTopics(userId));
    }
}
