package com.itc.linkedin.searchAndDiscover.service;

import com.itc.linkedin.searchAndDiscover.document.*;
import com.itc.linkedin.searchAndDiscover.dto.*;
import com.itc.linkedin.searchAndDiscover.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PeopleSearchRepository peopleRepository;
    private final PostSearchRepository postRepository;
    private final JobSearchRepository jobRepository;
    private final CompanySearchRepository companyRepository;

    public boolean hasSeedData() {
        return peopleRepository.count() > 0
                || postRepository.count() > 0
                || jobRepository.count() > 0
                || companyRepository.count() > 0;
    }

    public List<PeopleSearchResponse> searchPeople(String q, String userId) {
        return search(
                q,
                peopleRepository.findAll(),
                person -> scoreFields(
                        q,
                        weightedField(person.getFullName(), 14),
                        weightedField(person.getHeadline(), 10),
                        weightedField(person.getSkills(), 8),
                        weightedField(person.getLocation(), 4)
                ),
                person -> new PeopleSearchResponse(
                        person.getId(),
                        person.getFullName(),
                        person.getHeadline(),
                        person.getLocation(),
                        null,
                        "2nd"
                )
        );
    }

    public List<PostSearchResponse> searchPosts(String q, String userId) {
        return search(
                q,
                postRepository.findAll(),
                post -> scoreFields(
                                q,
                                weightedField(post.getAuthorName(), 10),
                                weightedField(post.getContent(), 12)
                        )
                        + Math.min(post.getLikes() / 20, 5)
                        + Math.min(post.getComments() / 10, 4),
                post -> new PostSearchResponse(
                        post.getId(),
                        post.getAuthorName(),
                        post.getContent(),
                        post.getLikes(),
                        post.getComments()
                )
        );
    }

    public List<JobSearchResponse> searchJobs(String q, String userId) {
        return search(
                q,
                jobRepository.findAll(),
                job -> scoreFields(
                                q,
                                weightedField(job.getTitle(), 14),
                                weightedField(job.getCompanyName(), 8),
                                weightedField(job.getLocation(), 6),
                                weightedField(job.getWorkplaceType(), 5)
                        )
                        + boostIfPhraseMatch(job.getWorkplaceType(), q, 2),
                job -> new JobSearchResponse(
                        job.getId(),
                        job.getTitle(),
                        job.getCompanyName(),
                        job.getLocation(),
                        job.getWorkplaceType()
                )
        );
    }

    public List<CompanySearchResponse> searchCompanies(String q, String userId) {
        return search(
                q,
                companyRepository.findAll(),
                company -> scoreFields(
                                q,
                                weightedField(company.getName(), 14),
                                weightedField(company.getIndustry(), 10),
                                weightedField(company.getLocation(), 6)
                        )
                        + Math.min(company.getFollowers() / 5000, 6),
                company -> new CompanySearchResponse(
                        company.getId(),
                        company.getName(),
                        company.getIndustry(),
                        company.getLocation(),
                        company.getFollowers()
                )
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

    private <T, R> List<R> search(
            String query,
            Iterable<T> source,
            ToIntFunction<T> scorer,
            Function<T, R> mapper
    ) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        return StreamSupport.stream(source.spliterator(), false)
                .map(item -> new ScoredResult<>(item, scorer.applyAsInt(item)))
                .filter(result -> result.score() > 0)
                .sorted(Comparator.comparingInt(ScoredResult<T>::score).reversed())
                .map(result -> mapper.apply(result.item()))
                .toList();
    }

    @SafeVarargs
    private static int scoreFields(String query, WeightedField... fields) {
        String normalizedQuery = normalize(query);
        List<String> queryTokens = tokenize(normalizedQuery);

        int combinedCoverage = fieldsMatchCoverage(queryTokens, fields);
        if (combinedCoverage == 0) {
            return 0;
        }

        int score = combinedCoverage * 6;
        for (WeightedField field : fields) {
            score += scoreField(normalizedQuery, queryTokens, field);
        }
        return score;
    }

    private static int fieldsMatchCoverage(List<String> queryTokens, WeightedField[] fields) {
        if (queryTokens.isEmpty()) {
            return 0;
        }

        int matches = 0;
        for (String token : queryTokens) {
            boolean matched = Arrays.stream(fields)
                    .map(WeightedField::value)
                    .anyMatch(value -> containsWordPrefix(value, token) || value.contains(token));
            if (!matched) {
                return 0;
            }
            matches++;
        }
        return matches;
    }

    private static int scoreField(String normalizedQuery, List<String> queryTokens, WeightedField field) {
        String value = field.value();
        if (value.isEmpty()) {
            return 0;
        }

        int score = 0;
        if (value.equals(normalizedQuery)) {
            score += field.weight() * 12;
        }
        if (value.startsWith(normalizedQuery)) {
            score += field.weight() * 8;
        }
        if (containsWordPrefix(value, normalizedQuery)) {
            score += field.weight() * 6;
        }
        if (value.contains(normalizedQuery)) {
            score += field.weight() * 4;
        }

        for (String token : queryTokens) {
            if (containsWordPrefix(value, token)) {
                score += field.weight() * 3;
            } else if (value.contains(token)) {
                score += field.weight() * 2;
            }
        }

        return score;
    }

    private static int boostIfPhraseMatch(String value, String query, int boost) {
        return normalize(value).contains(normalize(query)) ? boost : 0;
    }

    private static WeightedField weightedField(String value, int weight) {
        return new WeightedField(normalize(value), weight);
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static List<String> tokenize(String value) {
        if (value.isBlank()) {
            return List.of();
        }
        return List.of(value.split(" "));
    }

    private static boolean containsWordPrefix(String value, String token) {
        if (value.isEmpty() || token.isEmpty()) {
            return false;
        }

        return Arrays.stream(value.split(" "))
                .anyMatch(word -> word.startsWith(token));
    }

    private record WeightedField(String value, int weight) {
    }

    private record ScoredResult<T>(T item, int score) {
    }
}
