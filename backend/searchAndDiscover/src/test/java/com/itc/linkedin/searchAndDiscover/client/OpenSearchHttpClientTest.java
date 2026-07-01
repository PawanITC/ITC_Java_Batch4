package com.itc.linkedin.searchAndDiscover.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenSearchHttpClientTest {

    private HttpServer server;
    private String baseUrl;
    private OpenSearchHttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.start();
        baseUrl = "http://localhost:" + server.getAddress().getPort();

        client = new OpenSearchHttpClient(new ObjectMapper());
        ReflectionTestUtils.setField(client, "openSearchUrl", baseUrl);
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void bulkIndexSendsOnlyNdjsonContentType() {
        AtomicReference<List<String>> contentTypes = new AtomicReference<>();

        server.createContext("/_bulk", exchange -> {
            contentTypes.set(exchange.getRequestHeaders().get(HttpHeaders.CONTENT_TYPE));
            exchange.getRequestBody().readAllBytes();

            byte[] response = "{\"errors\":false}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        client.bulkIndex("people", List.of(new TestDocument("user-1", "Shubhra")), TestDocument::id);

        assertEquals(List.of("application/x-ndjson"), contentTypes.get());
    }

    private record TestDocument(String id, String name) {
    }
}
