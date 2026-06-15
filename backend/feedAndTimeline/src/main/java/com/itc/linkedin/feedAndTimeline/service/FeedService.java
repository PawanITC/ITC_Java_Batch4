package com.itc.linkedin.feedAndTimeline.service;

import com.itc.linkedin.feedAndTimeline.dto.*;
import com.itc.linkedin.feedAndTimeline.entity.Comment;
import com.itc.linkedin.feedAndTimeline.entity.Post;
import com.itc.linkedin.feedAndTimeline.entity.PostLike;
import com.itc.linkedin.feedAndTimeline.repository.CommentRepository;
import com.itc.linkedin.feedAndTimeline.repository.PostLikeRepository;
import com.itc.linkedin.feedAndTimeline.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    public List<FeedPostResponse> getFeed() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapPost)
                .toList();
    }

    public FeedPostResponse createPost(
            String userId,
            String username,
            CreatePostRequest request
    ) {
        Post post = Post.builder()
                .authorId(userId)
                .authorName(username)
                .authorHeadline("LinkedIn Member")
                .content(request.getContent())
                .likesCount(0)
                .commentsCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return mapPost(postRepository.save(post));
    }

    public FeedPostResponse getPost(Long postId) {
        Post post = findPost(postId);
        return mapPost(post);
    }

    public void deletePost(Long postId, String userId) {
        Post post = findPost(postId);

        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("You can only delete your own post");
        }

        postRepository.delete(post);
    }

    public FeedPostResponse likePost(Long postId, String userId) {
        Post post = findPost(postId);

        if (!postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            PostLike like = PostLike.builder()
                    .postId(postId)
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .build();

            postLikeRepository.save(like);

            post.setLikesCount(post.getLikesCount() + 1);
            postRepository.save(post);
        }

        return mapPost(post);
    }

    public FeedPostResponse unlikePost(Long postId, String userId) {
        Post post = findPost(postId);

        postLikeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(like -> {
                    postLikeRepository.delete(like);
                    post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
                    postRepository.save(post);
                });

        return mapPost(post);
    }

    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::mapComment)
                .toList();
    }

    public CommentResponse addComment(
            Long postId,
            String userId,
            String username,
            CreateCommentRequest request
    ) {
        Post post = findPost(postId);

        Comment comment = Comment.builder()
                .postId(postId)
                .authorId(userId)
                .authorName(username)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return mapComment(commentRepository.save(comment));
    }

    public void deleteComment(Long commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthorId().equals(userId)) {
            throw new RuntimeException("You can only delete your own comment");
        }

        Post post = findPost(comment.getPostId());
        post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
        postRepository.save(post);

        commentRepository.delete(comment);
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    private FeedPostResponse mapPost(Post post) {
        return FeedPostResponse.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .authorName(post.getAuthorName())
                .authorHeadline(post.getAuthorHeadline())
                .content(post.getContent())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private CommentResponse mapComment(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .authorId(comment.getAuthorId())
                .authorName(comment.getAuthorName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}