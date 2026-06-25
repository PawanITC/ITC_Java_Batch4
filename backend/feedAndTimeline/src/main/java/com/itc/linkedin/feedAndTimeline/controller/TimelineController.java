package com.itc.linkedin.feedAndTimeline.controller;

import com.itc.linkedin.feedAndTimeline.dto.response.TimelinePostResponse;
import com.itc.linkedin.feedAndTimeline.security.CurrentUserService;
import com.itc.linkedin.feedAndTimeline.service.TimelineService;
import com.itc.linkedin.feedAndTimeline.service.TimelineSortMode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;
    private final CurrentUserService currentUserService;

    @GetMapping({"","/"})
    public List<TimelinePostResponse> getMyTimeline(
            Authentication authentication,
            @RequestParam(name = "sort", defaultValue = "top") String sort
    ) {
        String userId = currentUserService.getUserId(authentication);
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Missing user identity in JWT");
        }
        return timelineService.getTimeline(userId, TimelineSortMode.from(sort));
    }

    @GetMapping("/{userId}")
    public List<TimelinePostResponse> getTimelineByUserId(
            @PathVariable String userId,
            @RequestParam(name = "sort", defaultValue = "top") String sort
    ) {
        return timelineService.getTimeline(userId, TimelineSortMode.from(sort));
    }
}
