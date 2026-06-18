package com.itc.linkedin.feedAndTimeline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itc.linkedin.feedAndTimeline.dto.*;
import com.itc.linkedin.feedAndTimeline.service.FeedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeedController.class)
@AutoConfigureMockMvc(addFilters = true)
@TestPropertySource(properties = {
        "KEYCLOAK_ISSUER_URI=http://localhost:8080/realms/test",
        "KEYCLOAK_JWK_SET_URI=http://localhost:8080/realms/test/protocol/openid-connect/certs"
})
class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private FeedService feedService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldGetFeed() throws Exception {
        when(feedService.getFeed()).thenReturn(List.of(samplePostResponse()));

        mockMvc.perform(get("/api/feed")
                        .with(jwt().jwt(this::jwtClaims)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].authorName").value("Demo User"))
                .andExpect(jsonPath("$.data[0].content").value("Hello LinkedIn"));
    }

    @Test
    void shouldCreatePost() throws Exception {
        CreatePostRequest request = new CreatePostRequest();
        request.setContent("New post");

        FeedPostResponse response = samplePostResponse();
        response.setContent("New post");

        when(feedService.createPost(
                eq("user-1"),
                eq("user.demo"),
                any(CreatePostRequest.class)
        )).thenReturn(response);

        mockMvc.perform(post("/api/feed/posts")
                        .with(jwt().jwt(this::jwtClaims))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("New post"));
    }

    @Test
    void shouldGetPostById() throws Exception {
        when(feedService.getPost(1L)).thenReturn(samplePostResponse());

        mockMvc.perform(get("/api/feed/posts/1")
                        .with(jwt().jwt(this::jwtClaims)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void shouldDeletePost() throws Exception {
        doNothing().when(feedService).deletePost(1L, "user-1");

        mockMvc.perform(delete("/api/feed/posts/1")
                        .with(jwt().jwt(this::jwtClaims))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void shouldLikePost() throws Exception {
        FeedPostResponse response = samplePostResponse();
        response.setLikesCount(1);

        when(feedService.likePost(1L, "user-1")).thenReturn(response);

        mockMvc.perform(post("/api/feed/posts/1/like")
                        .with(jwt().jwt(this::jwtClaims))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.likesCount").value(1));
    }

    @Test
    void shouldUnlikePost() throws Exception {
        when(feedService.unlikePost(1L, "user-1")).thenReturn(samplePostResponse());

        mockMvc.perform(delete("/api/feed/posts/1/like")
                        .with(jwt().jwt(this::jwtClaims))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.likesCount").value(0));
    }

    @Test
    void shouldGetComments() throws Exception {
        when(feedService.getComments(1L)).thenReturn(List.of(sampleCommentResponse()));

        mockMvc.perform(get("/api/feed/posts/1/comments")
                        .with(jwt().jwt(this::jwtClaims)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].content").value("Nice post"));
    }

    @Test
    void shouldAddComment() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("Nice post");

        when(feedService.addComment(
                eq(1L),
                eq("user-1"),
                eq("user.demo"),
                any(CreateCommentRequest.class)
        )).thenReturn(sampleCommentResponse());

        mockMvc.perform(post("/api/feed/posts/1/comments")
                        .with(jwt().jwt(this::jwtClaims))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("Nice post"));
    }

    @Test
    void shouldDeleteComment() throws Exception {
        doNothing().when(feedService).deleteComment(1L, "user-1");

        mockMvc.perform(delete("/api/feed/comments/1")
                        .with(jwt().jwt(this::jwtClaims))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    private void jwtClaims(Jwt.Builder jwt) {
        jwt.subject("user-1");
        jwt.claim("preferred_username", "user.demo");
        jwt.claim("email", "user.demo@example.com");
    }

    private FeedPostResponse samplePostResponse() {
        return FeedPostResponse.builder()
                .id(1L)
                .authorId("user-1")
                .authorName("Demo User")
                .authorHeadline("LinkedIn Member")
                .content("Hello LinkedIn")
                .likesCount(0)
                .commentsCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private CommentResponse sampleCommentResponse() {
        return CommentResponse.builder()
                .id(1L)
                .postId(1L)
                .authorId("user-1")
                .authorName("Demo User")
                .content("Nice post")
                .createdAt(LocalDateTime.now())
                .build();
    }
}