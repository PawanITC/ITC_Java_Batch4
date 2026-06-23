package com.itc.linkedin.feedAndTimeline.controller;

import com.itc.linkedin.feedAndTimeline.dto.response.TimelinePostResponse;
import com.itc.linkedin.feedAndTimeline.service.TimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    @GetMapping({"","/"})
    public List<TimelinePostResponse> getMyTimeline(
            @RequestHeader("X-User-Id") String userId
    ) {
        return timelineService.getTimeline(userId);
    }

    @GetMapping("/{userId}")
    public List<TimelinePostResponse> getTimelineByUserId(
            @PathVariable String userId
    ) {
        return timelineService.getTimeline(userId);
    }
}