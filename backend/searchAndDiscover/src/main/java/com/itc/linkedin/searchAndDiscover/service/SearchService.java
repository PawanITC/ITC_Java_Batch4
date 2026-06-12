package com.itc.linkedin.searchAndDiscover.service;

import com.itc.linkedin.searchAndDiscover.document.*;
import com.itc.linkedin.searchAndDiscover.dto.*;
import com.itc.linkedin.searchAndDiscover.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PeopleSearchRepository peopleRepository;
    private final PostSearchRepository postRepository;
    private final JobSearchRepository jobRepository;
    private final CompanySearchRepository companyRepository;

    public List<PeopleSearchResponse> searchPeople(String q, String userId) {
        return peopleRepository
                .findByFullNameContainingOrHeadlineContainingOrSkillsContaining(q, q, q)
                .stream()
                .map(p -> new PeopleSearchResponse(
                        p.getId(),
                        p.getFullName(),
                        p.getHeadline(),
                        p.getLocation(),
                        null,
                        "2nd"
                ))
                .toList();
    }

    public List<PostSearchResponse> searchPosts(String q, String userId) {
        return postRepository
                .findByAuthorNameContainingOrContentContaining(q, q)
                .stream()
                .map(p -> new PostSearchResponse(
                        p.getId(),
                        p.getAuthorName(),
                        p.getContent(),
                        p.getLikes(),
                        p.getComments()
                ))
                .toList();
    }

    public List<JobSearchResponse> searchJobs(String q, String userId) {
        return jobRepository
                .findByTitleContainingOrCompanyNameContainingOrLocationContaining(q, q, q)
                .stream()
                .map(j -> new JobSearchResponse(
                        j.getId(),
                        j.getTitle(),
                        j.getCompanyName(),
                        j.getLocation(),
                        j.getWorkplaceType()
                ))
                .toList();
    }

    public List<CompanySearchResponse> searchCompanies(String q, String userId) {
        return companyRepository
                .findByNameContainingOrIndustryContainingOrLocationContaining(q, q, q)
                .stream()
                .map(c -> new CompanySearchResponse(
                        c.getId(),
                        c.getName(),
                        c.getIndustry(),
                        c.getLocation(),
                        c.getFollowers()
                ))
                .toList();
    }

    public void seedAll() {
        peopleRepository.saveAll(List.of(
                PeopleDocument.builder()
                        .id("user-1")
                        .fullName("Shubhra Tripathi")
                        .headline("Java Full Stack Developer")
                        .location("United Kingdom")
                        .skills("Java Spring Boot React Kubernetes")
                        .build(),

                PeopleDocument.builder()
                        .id("user-2")
                        .fullName("Ananya Sharma")
                        .headline("Spring Boot Developer")
                        .location("London")
                        .skills("Java Spring Boot Microservices")
                        .build()
        ));

        postRepository.saveAll(List.of(
                PostDocument.builder()
                        .id("post-1")
                        .authorName("Shubhra Tripathi")
                        .content("Learning Kafka, API Gateway, Keycloak and Kubernetes.")
                        .likes(120)
                        .comments(15)
                        .build(),

                PostDocument.builder()
                        .id("post-2")
                        .authorName("Ananya Sharma")
                        .content("Spring Boot microservices with OpenSearch are powerful.")
                        .likes(88)
                        .comments(9)
                        .build()
        ));

        jobRepository.saveAll(List.of(
                JobDocument.builder()
                        .id("job-1")
                        .title("Java Backend Developer")
                        .companyName("LinkedIn Demo Company")
                        .location("London")
                        .workplaceType("Hybrid")
                        .build(),

                JobDocument.builder()
                        .id("job-2")
                        .title("Spring Boot Engineer")
                        .companyName("Tech Talent Ltd")
                        .location("Remote")
                        .workplaceType("Remote")
                        .build()
        ));

        companyRepository.saveAll(List.of(
                CompanyDocument.builder()
                        .id("company-1")
                        .name("LinkedIn Demo Ltd")
                        .industry("Technology")
                        .location("London")
                        .followers(25000)
                        .build(),

                CompanyDocument.builder()
                        .id("company-2")
                        .name("Cloud Careers")
                        .industry("Cloud Computing")
                        .location("Manchester")
                        .followers(12000)
                        .build()
        ));
    }
}