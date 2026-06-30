package com.itc.linkedin.searchAndDiscover.service;

import com.itc.linkedin.searchAndDiscover.document.*;
import com.itc.linkedin.searchAndDiscover.dto.*;
import com.itc.linkedin.searchAndDiscover.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PeopleSearchRepository peopleRepository;
    private final PostSearchRepository postRepository;
    private final JobSearchRepository jobRepository;
    private final CompanySearchRepository companyRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public boolean hasSeedData() {
        return peopleRepository.count() > 0
                || postRepository.count() > 0
                || jobRepository.count() > 0
                || companyRepository.count() > 0;
    }

    public List<PeopleSearchResponse> searchPeople(String q, String userId) {
        return search(
                q,
                PeopleDocument.class,
                person -> new PeopleSearchResponse(
                        person.getId(),
                        person.getFullName(),
                        person.getHeadline(),
                        person.getLocation(),
                        null,
                        "2nd"
                ),
                "fullName^6",
                "headline^4",
                "skills^3",
                "location"
        );
    }

    public List<PostSearchResponse> searchPosts(String q, String userId) {
        return search(
                q,
                PostDocument.class,
                post -> new PostSearchResponse(
                        post.getId(),
                        post.getAuthorName(),
                        post.getContent(),
                        post.getLikes(),
                        post.getComments()
                ),
                "content^5",
                "authorName^3"
        );
    }

    public List<JobSearchResponse> searchJobs(String q, String userId) {
        return search(
                q,
                JobDocument.class,
                job -> new JobSearchResponse(
                        job.getId(),
                        job.getTitle(),
                        job.getCompanyName(),
                        job.getLocation(),
                        job.getWorkplaceType()
                ),
                "title^6",
                "companyName^3",
                "workplaceType^2",
                "location"
        );
    }

    public List<CompanySearchResponse> searchCompanies(String q, String userId) {
        return search(
                q,
                CompanyDocument.class,
                company -> new CompanySearchResponse(
                        company.getId(),
                        company.getName(),
                        company.getIndustry(),
                        company.getLocation(),
                        company.getFollowers()
                ),
                "name^6",
                "industry^4",
                "location"
        );
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

    private <T, R> List<R> search(String query, Class<T> documentClass, Function<T, R> mapper, String... fields) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(bool -> bool
                        .should(s -> s.multiMatch(match -> match
                                .query(query.trim())
                                .fields(List.of(fields))
                                .operator(co.elastic.clients.elasticsearch._types.query_dsl.Operator.And)
                        ))
                        .should(s -> s.multiMatch(match -> match
                                .query(query.trim())
                                .fields(List.of(fields))
                                .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.PhrasePrefix)
                                .boost(1.5f)
                        ))
                        .minimumShouldMatch("1")
                ))
                .withMaxResults(20)
                .build();

        return elasticsearchOperations.search(nativeQuery, documentClass)
                .getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .map(mapper)
                .toList();
    }
}
