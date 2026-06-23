package com.itc.linkedin.postandtimeline.controller;

import com.itc.linkedin.postandtimeline.dto.request.CreatePostRequest;
import com.itc.linkedin.postandtimeline.dto.response.PostResponse;
import com.itc.linkedin.postandtimeline.security.CurrentUserService;
import com.itc.linkedin.postandtimeline.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PostController postController;

    @Test
    void shouldUseExplicitHeadersOverJwtFallback() {
        CreatePostRequest request = new CreatePostRequest("content");
        PostResponse response = response();

        when(postService.createPost("header-user", "header-name", request)).thenReturn(response);

        PostResponse actual = postController.createPost(
                "header-user",
                "header-name",
                authentication,
                request
        );

        assertThat(actual).isEqualTo(response);
        verify(postService).createPost("header-user", "header-name", request);
    }

    @Test
    void shouldFallbackToCurrentUserServiceWhenHeadersMissing() {
        CreatePostRequest request = new CreatePostRequest("content");
        PostResponse response = response();

        when(currentUserService.getUserId(authentication)).thenReturn("jwt-user");
        when(currentUserService.getUsername(authentication)).thenReturn("jwt-name");
        when(postService.createPost("jwt-user", "jwt-name", request)).thenReturn(response);

        PostResponse actual = postController.createPost(null, null, authentication, request);

        assertThat(actual).isEqualTo(response);
        verify(postService).createPost("jwt-user", "jwt-name", request);
    }

    @Test
    void shouldRejectRequestWhenIdentityMissing() {
        when(currentUserService.getUserId(authentication)).thenReturn(null);
        when(currentUserService.getUsername(authentication)).thenReturn(null);

        assertThatThrownBy(() -> postController.createPost(null, null, authentication, new CreatePostRequest("content")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401 UNAUTHORIZED")
                .hasMessageContaining("Missing user identity");
    }

    private PostResponse response() {
        return PostResponse.builder()
                .id(1L)
                .authorId("user")
                .authorName("name")
                .authorHeadline("headline")
                .content("content")
                .likesCount(0)
                .commentsCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
