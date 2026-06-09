package com.itc.linkedin.searchAndDiscover.service;

import com.itc.linkedin.searchAndDiscover.dto.CompanySearchResponse;
import com.itc.linkedin.searchAndDiscover.dto.JobSearchResponse;
import com.itc.linkedin.searchAndDiscover.dto.PeopleSearchResponse;
import com.itc.linkedin.searchAndDiscover.dto.PostSearchResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    public List<PeopleSearchResponse> searchPeople(String query) {
        return List.of(
                new PeopleSearchResponse(
                        "user-1",
                        "Shubhra Tripathi",
                        "Java Full Stack Developer",
                        "United Kingdom",
                        null,
                        "2nd"
                ),
                new PeopleSearchResponse(
                        "user-2",
                        "Demo Recruiter",
                        "Technical Recruiter hiring Java Developers",
                        "London",
                        null,
                        "3rd"
                )
        );
    }

    public List<PostSearchResponse> searchPosts(String query) {
        return List.of(
                new PostSearchResponse(
                        "post-1",
                        "Shubhra Tripathi",
                        "Learning Kafka, API Gateway and Kubernetes.",
                        120,
                        15
                )
        );
    }

    public List<JobSearchResponse> searchJobs(String query) {
        return List.of(
                new JobSearchResponse(
                        "job-1",
                        "Java Backend Developer",
                        "LinkedIn Demo Company",
                        "London",
                        "Hybrid"
                )
        );
    }

    public List<CompanySearchResponse> searchCompanies(String query) {
        return List.of(
                new CompanySearchResponse(
                        "company-1",
                        "LinkedIn Demo Ltd",
                        "Technology",
                        "London",
                        25000
                )
        );
    }


}
