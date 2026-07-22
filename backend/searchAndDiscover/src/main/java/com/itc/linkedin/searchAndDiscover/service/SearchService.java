package com.itc.linkedin.searchAndDiscover.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itc.linkedin.searchAndDiscover.document.*;
import com.itc.linkedin.searchAndDiscover.dto.*;
import com.itc.linkedin.searchAndDiscover.client.OpenSearchHttpClient;
import com.itc.linkedin.searchAndDiscover.kafka.event.CommentCreatedEvent;
import com.itc.linkedin.searchAndDiscover.kafka.event.PostCreatedEvent;
import com.itc.linkedin.searchAndDiscover.kafka.event.PostDeletedEvent;
import com.itc.linkedin.searchAndDiscover.kafka.event.PostLikedEvent;
import com.itc.linkedin.searchAndDiscover.kafka.event.ProfileDeletedEvent;
import com.itc.linkedin.searchAndDiscover.kafka.event.ProfileIndexEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final String PEOPLE_INDEX = "people";
    private static final String POSTS_INDEX = "posts";
    private static final String JOBS_INDEX = "jobs";
    private static final String COMPANIES_INDEX = "companies";

    private final OpenSearchHttpClient openSearchClient;
    private final ObjectMapper objectMapper;

    public boolean hasSeedData() {
        return openSearchClient.hasDocuments(PEOPLE_INDEX)
                || openSearchClient.hasDocuments(POSTS_INDEX)
                || openSearchClient.hasDocuments(JOBS_INDEX)
                || openSearchClient.hasDocuments(COMPANIES_INDEX);
    }

    public List<PeopleSearchResponse> searchPeople(String q, String userId) {
        return search(
                q,
                PEOPLE_INDEX,
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

    public void indexProfile(ProfileIndexEvent event) {
        if (Boolean.FALSE.equals(event.profilePublic())) {
            deleteProfile(new ProfileDeletedEvent(
                    event.eventId(),
                    "profile.deleted",
                    event.eventVersion(),
                    event.occurredAt(),
                    event.profileId(),
                    event.keycloakUserId()
            ));
            return;
        }

        openSearchClient.bulkIndex(PEOPLE_INDEX, List.of(toPeopleDocument(event)), PeopleDocument::getId);
    }

    public void indexProfiles(List<ProfileIndexEvent> profiles) {
        List<PeopleDocument> documents = profiles.stream()
                .filter(profile -> !Boolean.FALSE.equals(profile.profilePublic()))
                .map(this::toPeopleDocument)
                .toList();

        openSearchClient.bulkIndex(PEOPLE_INDEX, documents, PeopleDocument::getId);
    }

    public void deleteProfile(ProfileDeletedEvent event) {
        openSearchClient.deleteDocument(PEOPLE_INDEX, event.profileId());
    }

    public List<PostSearchResponse> searchPosts(String q, String userId) {
        return search(
                q,
                POSTS_INDEX,
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

    public void indexPost(PostCreatedEvent event) {
        openSearchClient.bulkIndex(POSTS_INDEX, List.of(
                PostDocument.builder()
                        .id(String.valueOf(event.postId()))
                        .authorName(event.authorName())
                        .content(event.content())
                        .likes(0)
                        .comments(0)
                        .build()
        ), PostDocument::getId);
    }

    public void indexPosts(List<PostCreatedEvent> events) {
        openSearchClient.bulkIndex(
                POSTS_INDEX,
                events.stream()
                        .map(event -> PostDocument.builder()
                                .id(String.valueOf(event.postId()))
                                .authorName(event.authorName())
                                .content(event.content())
                                .likes(0)
                                .comments(0)
                                .build())
                        .toList(),
                PostDocument::getId
        );
    }

    public void deletePost(PostDeletedEvent event) {
        openSearchClient.deleteDocument(POSTS_INDEX, String.valueOf(event.postId()));
    }

    public void updatePostLikes(PostLikedEvent event) {
        openSearchClient.updateDocument(
                POSTS_INDEX,
                String.valueOf(event.postId()),
                Map.of("likes", event.likesCount())
        );
    }

    public void updatePostComments(CommentCreatedEvent event) {
        openSearchClient.updateDocument(
                POSTS_INDEX,
                String.valueOf(event.postId()),
                Map.of("comments", event.commentsCount())
        );
    }

    public List<JobSearchResponse> searchJobs(String q, String userId) {
        return search(
                q,
                JOBS_INDEX,
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
                COMPANIES_INDEX,
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
        openSearchClient.bulkIndex(PEOPLE_INDEX, List.of(
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
        ), PeopleDocument::getId);

        openSearchClient.bulkIndex(POSTS_INDEX, List.of(
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
        ), PostDocument::getId);

        openSearchClient.bulkIndex(JOBS_INDEX, List.of(
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
        ), JobDocument::getId);

        openSearchClient.bulkIndex(COMPANIES_INDEX, List.of(
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
        ), CompanyDocument::getId);
    }

    private <T, R> List<R> search(
            String query,
            String indexName,
            Class<T> documentClass,
            Function<T, R> mapper,
            String... fields
    ) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        return openSearchClient.search(indexName, searchQuery(query.trim(), fields), documentClass)
                .stream()
                .map(mapper)
                .toList();
    }

    private JsonNode searchQuery(String query, String... fields) {
        Map<String, Object> exactMatch = Map.of(
                "multi_match", Map.of(
                        "query", query,
                        "fields", List.of(fields),
                        "operator", "and"
                )
        );
        Map<String, Object> prefixMatch = Map.of(
                "multi_match", Map.of(
                        "query", query,
                        "fields", List.of(fields),
                        "type", "phrase_prefix",
                        "boost", 1.5
                )
        );

        Map<String, Object> bool = new LinkedHashMap<>();
        bool.put("should", List.of(exactMatch, prefixMatch));
        bool.put("minimum_should_match", 1);

        return objectMapper.valueToTree(Map.of(
                "size", 20,
                "query", Map.of("bool", bool)
        ));
    }

    private PeopleDocument toPeopleDocument(ProfileIndexEvent event) {
        return PeopleDocument.builder()
                .id(event.profileId())
                .fullName(fullName(event.firstName(), event.lastName()))
                .headline(event.headline())
                .location(location(event.city(), event.country()))
                .skills("")
                .build();
    }

    private String fullName(String firstName, String lastName) {
        return Stream.of(firstName, lastName)
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + " " + right)
                .orElse("");
    }

    private String location(String city, String country) {
        return Stream.of(city, country)
                .filter(value -> value != null && !value.isBlank())
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
    }
}
