package com.itc.linkedin.searchAndDiscover.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @GetMapping("/people")
    public List<Map<String, String>> searchPeople(@RequestParam String q) {
        return List.of(
                Map.of(
                        "id", "1",
                        "name", "Shubhra Tripathi",
                        "headline", "Java Full Stack Developer",
                        "skills", "Java, Spring Boot, React"
                ),
                Map.of(
                        "id", "2",
                        "name", "Demo Recruiter",
                        "headline", "Technical Recruiter",
                        "skills", "Hiring, Java, Backend"
                )
        );
    }
}
