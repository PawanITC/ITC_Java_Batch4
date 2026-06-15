package com.itc.linkedin.feedAndTimeline.controller;

import com.itc.linkedin.feedAndTimeline.dto.*;
import com.itc.linkedin.feedAndTimeline.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ApiResponse<List<FeedPostResponse>> getFeed() {
        return ApiResponse.success(feedService.getFeed());
    }

    @PostMapping("/posts")
    public ApiResponse<FeedPostResponse> createPost(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreatePostRequest request
    ) {
        String userId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");

        return ApiResponse.success(
                feedService.createPost(userId, username, request)
        );
    }

    @GetMapping("/posts/{postId}")
    public ApiResponse<FeedPostResponse> getPost(
            @PathVariable Long postId
    ) {
        return ApiResponse.success(feedService.getPost(postId));
    }

    @DeleteMapping("/posts/{postId}")
    public ApiResponse<String> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        feedService.deletePost(postId, jwt.getSubject());
        return ApiResponse.success("Post deleted successfully");
    }

    @PostMapping("/posts/{postId}/like")
    public ApiResponse<FeedPostResponse> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ApiResponse.success(
                feedService.likePost(postId, jwt.getSubject())
        );
    }

    @DeleteMapping("/posts/{postId}/like")
    public ApiResponse<FeedPostResponse> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ApiResponse.success(
                feedService.unlikePost(postId, jwt.getSubject())
        );
    }

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<List<CommentResponse>> getComments(
            @PathVariable Long postId
    ) {
        return ApiResponse.success(feedService.getComments(postId));
    }

    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<CommentResponse> addComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateCommentRequest request
    ) {
        String userId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");

        return ApiResponse.success(
                feedService.addComment(postId, userId, username, request)
        );
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<String> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        feedService.deleteComment(commentId, jwt.getSubject());
        return ApiResponse.success("Comment deleted successfully");
    }
}