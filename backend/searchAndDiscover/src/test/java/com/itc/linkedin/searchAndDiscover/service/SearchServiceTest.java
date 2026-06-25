package com.itc.linkedin.searchAndDiscover.service;

import com.itc.linkedin.searchAndDiscover.document.CompanyDocument;
import com.itc.linkedin.searchAndDiscover.document.JobDocument;
import com.itc.linkedin.searchAndDiscover.document.PeopleDocument;
import com.itc.linkedin.searchAndDiscover.document.PostDocument;
import com.itc.linkedin.searchAndDiscover.dto.CompanySearchResponse;
import com.itc.linkedin.searchAndDiscover.dto.JobSearchResponse;
import com.itc.linkedin.searchAndDiscover.dto.PeopleSearchResponse;
import com.itc.linkedin.searchAndDiscover.dto.PostSearchResponse;
import com.itc.linkedin.searchAndDiscover.repository.CompanySearchRepository;
import com.itc.linkedin.searchAndDiscover.repository.JobSearchRepository;
import com.itc.linkedin.searchAndDiscover.repository.PeopleSearchRepository;
import com.itc.linkedin.searchAndDiscover.repository.PostSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private PeopleSearchRepository peopleRepository;

    @Mock
    private PostSearchRepository postRepository;

    @Mock
    private JobSearchRepository jobRepository;

    @Mock
    private CompanySearchRepository companyRepository;

    @InjectMocks
    private SearchService searchService;

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

        when(peopleRepository.findAll()).thenReturn(List.of(weakerMatch, exactHeadline));

        List<PeopleSearchResponse> result = searchService.searchPeople("java full stack", "user-1");

        assertEquals(2, result.size());
        assertEquals("user-1", result.get(0).id());
    }

    @Test
    void shouldRequireCoverageAcrossQueryTokens() {
        PeopleDocument partialMatch = PeopleDocument.builder()
                .id("user-1")
                .fullName("Shubhra Tripathi")
                .headline("Java Developer")
                .location("London")
                .skills("Spring Boot")
                .build();

        when(peopleRepository.findAll()).thenReturn(List.of(partialMatch));

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

        when(postRepository.findAll()).thenReturn(List.of(weakMatch, strongMatch));

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

        when(jobRepository.findAll()).thenReturn(List.of(remoteJob));

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

        when(companyRepository.findAll()).thenReturn(List.of(weakerCompany, strongCompany));

        List<CompanySearchResponse> result = searchService.searchCompanies("cloud", "user-1");

        assertEquals("company-1", result.get(0).id());
    }
}
