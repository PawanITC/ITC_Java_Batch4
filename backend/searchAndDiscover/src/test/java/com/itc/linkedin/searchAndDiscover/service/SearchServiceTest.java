package com.itc.linkedin.searchAndDiscover.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itc.linkedin.searchAndDiscover.client.OpenSearchHttpClient;
import com.itc.linkedin.searchAndDiscover.document.CompanyDocument;
import com.itc.linkedin.searchAndDiscover.document.JobDocument;
import com.itc.linkedin.searchAndDiscover.document.PeopleDocument;
import com.itc.linkedin.searchAndDiscover.document.PostDocument;
import com.itc.linkedin.searchAndDiscover.dto.CompanySearchResponse;
import com.itc.linkedin.searchAndDiscover.dto.JobSearchResponse;
import com.itc.linkedin.searchAndDiscover.dto.PeopleSearchResponse;
import com.itc.linkedin.searchAndDiscover.dto.PostSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private OpenSearchHttpClient openSearchClient;

    private SearchService searchService;

    @BeforeEach
    void setUp() {
        searchService = new SearchService(openSearchClient, new ObjectMapper());
    }

    @Test
    void shouldRankPeopleByClosestMatch() {
        PeopleDocument exactHeadline = PeopleDocument.builder()
                .id("user-1")
                .fullName("Shubhra Tripathi")
                .headline("Java Full Stack Developer")
                .location("London")
                .skills("Java Spring Boot React")
                .build();

        PeopleDocument weakerMatch = PeopleDocument.builder()
                .id("user-2")
                .fullName("Ava Brown")
                .headline("Full Stack Engineer")
                .location("Manchester")
                .skills("Java")
                .build();

        when(openSearchClient.search(eq("people"), any(JsonNode.class), eq(PeopleDocument.class)))
                .thenReturn(List.of(exactHeadline, weakerMatch));

        List<PeopleSearchResponse> result = searchService.searchPeople("java full stack", "user-1");

        assertEquals(2, result.size());
        assertEquals("user-1", result.get(0).id());
    }

    @Test
    void shouldRequireCoverageAcrossQueryTokens() {
        when(openSearchClient.search(eq("people"), any(JsonNode.class), eq(PeopleDocument.class)))
                .thenReturn(List.of());

        List<PeopleSearchResponse> result = searchService.searchPeople("java kubernetes", "user-1");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRankPostsUsingRelevanceAndEngagement() {
        PostDocument strongMatch = PostDocument.builder()
                .id("post-1")
                .authorName("Shubhra Tripathi")
                .content("Java backend search patterns for LinkedIn style discovery")
                .likes(120)
                .comments(15)
                .build();

        PostDocument weakMatch = PostDocument.builder()
                .id("post-2")
                .authorName("Alex")
                .content("Learning Java")
                .likes(2)
                .comments(0)
                .build();

        when(openSearchClient.search(eq("posts"), any(JsonNode.class), eq(PostDocument.class)))
                .thenReturn(List.of(strongMatch, weakMatch));

        List<PostSearchResponse> result = searchService.searchPosts("java search", "user-1");

        assertEquals("post-1", result.get(0).id());
    }

    @Test
    void shouldSearchJobsAcrossRoleAndWorkplaceType() {
        JobDocument remoteJob = JobDocument.builder()
                .id("job-1")
                .title("Java Backend Developer")
                .companyName("LinkedIn Demo Company")
                .location("London")
                .workplaceType("Remote")
                .build();

        when(openSearchClient.search(eq("jobs"), any(JsonNode.class), eq(JobDocument.class)))
                .thenReturn(List.of(remoteJob));

        List<JobSearchResponse> result = searchService.searchJobs("java remote", "user-1");

        assertEquals(1, result.size());
        assertEquals("job-1", result.get(0).id());
    }

    @Test
    void shouldBoostPopularCompaniesWhenTextMatchIsComparable() {
        CompanyDocument strongCompany = CompanyDocument.builder()
                .id("company-1")
                .name("Cloud Careers")
                .industry("Cloud Computing")
                .location("London")
                .followers(25000)
                .build();

        CompanyDocument weakerCompany = CompanyDocument.builder()
                .id("company-2")
                .name("Cloud Collective")
                .industry("Cloud Computing")
                .location("London")
                .followers(5000)
                .build();

        when(openSearchClient.search(eq("companies"), any(JsonNode.class), eq(CompanyDocument.class)))
                .thenReturn(List.of(strongCompany, weakerCompany));

        List<CompanySearchResponse> result = searchService.searchCompanies("cloud", "user-1");

        assertEquals("company-1", result.get(0).id());
    }
}
