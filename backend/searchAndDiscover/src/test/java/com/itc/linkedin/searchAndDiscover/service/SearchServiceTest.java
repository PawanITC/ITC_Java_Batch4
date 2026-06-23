package com.itc.linkedin.searchAndDiscover.service;

import com.itc.linkedin.searchAndDiscover.document.PeopleDocument;
import com.itc.linkedin.searchAndDiscover.dto.PeopleSearchResponse;
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

import static org.junit.jupiter.api.Assertions.*;
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
    void shouldSearchPeople() {

        PeopleDocument person = PeopleDocument.builder()
                .id("user-1")
                .fullName("Shubhra Tripathi")
                .headline("Java Full Stack Developer")
                .location("United Kingdom")
                .skills("Java Spring Boot")
                .build();

        when(
                peopleRepository
                        .findByFullNameContainingOrHeadlineContainingOrSkillsContaining(
                                "java",
                                "java",
                                "java"
                        )
        ).thenReturn(List.of(person));

        List<PeopleSearchResponse> result =
                searchService.searchPeople(
                        "java",
                        "user-1"
                );

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnEmptyPeopleList() {

        when(
                peopleRepository
                        .findByFullNameContainingOrHeadlineContainingOrSkillsContaining(
                                "unknown",
                                "unknown",
                                "unknown"
                        )
        ).thenReturn(List.of());

        List<PeopleSearchResponse> result =
                searchService.searchPeople(
                        "unknown",
                        "user-1"
                );

        assertTrue(result.isEmpty());
    }
}