package com.itc.linkedin.searchAndDiscover.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class OpenSearchHttpClient {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Value("${spring.elasticsearch.uris}")
    private String openSearchUrl;

    @Value("${spring.elasticsearch.username:}")
    private String username;

    @Value("${spring.elasticsearch.password:}")
    private String password;

    public boolean hasDocuments(String indexName) {
        try {
            HttpRequest request = requestBuilder(indexName + "/_count")
                    .GET()
                    .build();
            HttpResponse<String> response = send(request);

            if (response.statusCode() == 404) {
                return false;
            }

            requireSuccess(response, "Count documents failed");
            return objectMapper.readTree(response.body()).path("count").asLong(0) > 0;
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Unable to count OpenSearch documents in " + indexName, error);
        } catch (IOException error) {
            throw new IllegalStateException("Unable to count OpenSearch documents in " + indexName, error);
        }
    }

    public <T> List<T> search(String indexName, JsonNode query, Class<T> resultType) {
        try {
            HttpRequest request = requestBuilder(indexName + "/_search")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(query)))
                    .build();
            HttpResponse<String> response = send(request);

            if (response.statusCode() == 404) {
                return List.of();
            }

            requireSuccess(response, "Search failed");

            List<T> results = new ArrayList<>();
            JsonNode hits = objectMapper.readTree(response.body()).path("hits").path("hits");
            if (!hits.isArray()) {
                return results;
            }

            for (JsonNode hit : hits) {
                JsonNode source = hit.path("_source");
                if (!source.isMissingNode()) {
                    results.add(objectMapper.treeToValue(source, resultType));
                }
            }

            return results;
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Unable to search OpenSearch index " + indexName, error);
        } catch (IOException error) {
            throw new IllegalStateException("Unable to search OpenSearch index " + indexName, error);
        }
    }

    public <T> void bulkIndex(String indexName, List<T> documents, Function<T, String> idExtractor) {
        if (documents.isEmpty()) {
            return;
        }

        try {
            StringBuilder body = new StringBuilder();
            for (T document : documents) {
                String id = idExtractor.apply(document);
                body.append(objectMapper.writeValueAsString(Map.of(
                        "index", Map.of(
                                "_index", indexName,
                                "_id", id
                        )
                ))).append("\n");
                body.append(objectMapper.writeValueAsString(document)).append("\n");
            }

            HttpRequest request = requestBuilder("_bulk")
                    .header(HttpHeaders.CONTENT_TYPE, "application/x-ndjson")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();
            HttpResponse<String> response = send(request);
            requireSuccess(response, "Bulk index failed");

            JsonNode responseBody = objectMapper.readTree(response.body());
            if (responseBody.path("errors").asBoolean(false)) {
                throw new IllegalStateException("Bulk index failed with item errors: " + response.body());
            }
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Unable to bulk index OpenSearch documents in " + indexName, error);
        } catch (IOException error) {
            throw new IllegalStateException("Unable to bulk index OpenSearch documents in " + indexName, error);
        }
    }

    private HttpRequest.Builder requestBuilder(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path))
                .timeout(Duration.ofSeconds(15))
                .header(HttpHeaders.ACCEPT, "application/json")
                .header(HttpHeaders.CONTENT_TYPE, "application/json");

        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            String credentials = username + ":" + password;
            builder.header(
                    HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8))
            );
        }

        return builder;
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private URI uri(String path) {
        String base = openSearchUrl.endsWith("/")
                ? openSearchUrl.substring(0, openSearchUrl.length() - 1)
                : openSearchUrl;
        return URI.create(base + "/" + path);
    }

    private void requireSuccess(HttpResponse<String> response, String message) {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(message + ": HTTP " + response.statusCode() + " " + response.body());
        }
    }
}
