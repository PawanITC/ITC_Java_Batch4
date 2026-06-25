package com.itc.linkedin.postandtimeline.controller;

import com.itc.linkedin.postandtimeline.dto.request.CreateCommentRequest;
import com.itc.linkedin.postandtimeline.dto.request.CreatePostRequest;
import com.itc.linkedin.postandtimeline.dto.response.PostResponse;
import com.itc.linkedin.postandtimeline.security.CurrentUserService;
import com.itc.linkedin.postandtimeline.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CurrentUserService currentUserService;

    @PostMapping
    public PostResponse createPost(
            Authentication authentication,
            @Valid @RequestBody CreatePostRequest request
    ) {
        String userId = currentUserService.getUserId(authentication);
        String username = currentUserService.getUsername(authentication);

        if (!StringUtils.hasText(userId) || !StringUtils.hasText(username)) {
            throw new ResponseStatusException(
                    UNAUTHORIZED,
                    "Missing user identity in JWT."
            );
        }

        return postService.createPost(userId, username, request);
    }

    @PostMapping("/{postId}/like")
    public PostResponse likePost(
            Authentication authentication,
            @PathVariable Long postId
    ) {
        return postService.likePost(postId, requiredUserId(authentication));
    }

    @DeleteMapping("/{postId}/like")
    public PostResponse unlikePost(
            Authentication authentication,
            @PathVariable Long postId
    ) {
        return postService.unlikePost(postId, requiredUserId(authentication));
    }

    @PostMapping("/{postId}/comments")
    public PostResponse addComment(
            Authentication authentication,
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return postService.addComment(
                postId,
                requiredUserId(authentication),
                requiredUsername(authentication),
                request
        );
    }

    @DeleteMapping("/{postId}")
    public void deletePost(
            Authentication authentication,
            @PathVariable Long postId
    ) {
        postService.deletePost(postId, requiredUserId(authentication));
    }

    private String requiredUserId(Authentication authentication) {
        String userId = currentUserService.getUserId(authentication);
        if (!StringUtils.hasText(userId)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Missing user identity in JWT.");
        }
        return userId;
    }

    private String requiredUsername(Authentication authentication) {
        String username = currentUserService.getUsername(authentication);
        if (!StringUtils.hasText(username)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Missing user identity in JWT.");
        }
        return username;
    }
}
