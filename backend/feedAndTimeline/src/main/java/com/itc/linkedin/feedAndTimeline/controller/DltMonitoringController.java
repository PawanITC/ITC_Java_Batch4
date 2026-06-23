package com.itc.linkedin.feedAndTimeline.controller;

import com.itc.linkedin.feedAndTimeline.service.DltMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal/kafka")
@RequiredArgsConstructor
public class DltMonitoringController {

    private final DltMonitoringService dltMonitoringService;

    @GetMapping("/dlt")
    public Map<String, Object> dltStatus() throws Exception {
        return dltMonitoringService.getDltStatus();
    }
}
