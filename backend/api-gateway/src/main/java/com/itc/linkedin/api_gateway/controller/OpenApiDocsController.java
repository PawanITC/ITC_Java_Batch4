package com.itc.linkedin.api_gateway.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class OpenApiDocsController {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    private final Map<String, ServiceDocs> serviceDocs;

    public OpenApiDocsController(
            @Value("${USERPROFILE_SERVICE_URI:http://userprofile-service:8083}") String userProfileServiceUri,
            @Value("${FEED_SERVICE_URI:http://feed-service:8087}") String feedServiceUri,
            @Value("${POST_SERVICE_URI:http://post-service:8088}") String postServiceUri,
            @Value("${SEARCH_SERVICE_URI:http://search-service:8086}") String searchServiceUri,
            @Value("${JOBPOSTING_SERVICE_URI:http://jobposting-service:8091}") String jobPostingServiceUri,
            @Value("${NOTIFICATION_SERVICE_URI:http://notification-service:8090}") String notificationServiceUri,
            @Value("${PAYMENT_SERVICE_URI:http://payment-service:8090}") String paymentServiceUri
        ) {
        this.objectMapper = JsonMapper.builder().build();
        this.webClient = WebClient.create();
        this.serviceDocs = Map.of(
                "userprofile", new ServiceDocs(userProfileServiceUri + "/api-docs", "/"),
                "feed", new ServiceDocs(feedServiceUri + "/v3/api-docs", "/"),
                "post", new ServiceDocs(postServiceUri + "/v3/api-docs", "/"),
                "search", new ServiceDocs(searchServiceUri + "/v3/api-docs", "/"),
                "jobs", new ServiceDocs(jobPostingServiceUri + "/v3/api-docs", "/"),
                "notifications", new ServiceDocs(notificationServiceUri + "/v3/api-docs", "/api/notifications"),
                "payments", new ServiceDocs(paymentServiceUri + "/v3/api-docs", "/api/payments")
        );
    }

    @GetMapping(value = "/v3/api-docs/{service}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> apiDocs(@PathVariable String service) {
        ServiceDocs docs = serviceDocs.get(service);
        if (docs == null) {
            return Mono.just(ResponseEntity.notFound().build());
        }

        return webClient.get()
                .uri(docs.docsUrl())
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(rewriteServers(body, docs.publicBasePath())));
    }

    private String rewriteServers(String body, String publicBasePath) {
        try {
            JsonNode parsed = objectMapper.readTree(body);
            if (!(parsed instanceof ObjectNode objectNode)) {
                return body;
            }

            ArrayNode servers = objectMapper.createArrayNode();
            servers.add(objectMapper.createObjectNode()
                    .put("url", publicBasePath)
                    .put("description", "API Gateway"));
            objectNode.set("servers", servers);
            return objectMapper.writeValueAsString(objectNode);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to rewrite OpenAPI docs", ex);
        }
    }

    private record ServiceDocs(String docsUrl, String publicBasePath) {
    }
}
