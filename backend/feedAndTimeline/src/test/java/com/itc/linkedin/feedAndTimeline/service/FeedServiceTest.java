package com.itc.linkedin.feedAndTimeline.service;

import com.itc.linkedin.feedAndTimeline.dto.*;
import com.itc.linkedin.feedAndTimeline.entity.Comment;
import com.itc.linkedin.feedAndTimeline.entity.Post;
import com.itc.linkedin.feedAndTimeline.entity.PostLike;
import com.itc.linkedin.feedAndTimeline.exception.ForbiddenActionException;
import com.itc.linkedin.feedAndTimeline.exception.ResourceNotFoundException;
import com.itc.linkedin.feedAndTimeline.repository.CommentRepository;
import com.itc.linkedin.feedAndTimeline.repository.PostLikeRepository;
import com.itc.linkedin.feedAndTimeline.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedServiceTest {

    private PostRepository postRepository;
    private PostLikeRepository postLikeRepository;
    private CommentRepository commentRepository;
    private FeedService feedService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        postLikeRepository = mock(PostLikeRepository.class);
        commentRepository = mock(CommentRepository.class);

        feedService = new FeedService(
                postRepository,
                postLikeRepository,
                commentRepository
        );
    }

    @Test
    void shouldReturnFeedPostsOrderedByCreatedAtDesc() {
        Post post = samplePost();

        when(postRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(post));

        List<FeedPostResponse> result = feedService.getFeed();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getAuthorName()).isEqualTo("Demo User");
        assertThat(result.get(0).getContent()).isEqualTo("Hello LinkedIn");

        verify(postRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void shouldCreatePost() {
        CreatePostRequest request = new CreatePostRequest();
        request.setContent("My first post");

        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> {
                    Post post = invocation.getArgument(0);
                    post.setId(1L);
                    return post;
                });

        FeedPostResponse result =
                feedService.createPost("user-1", "Demo User", request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAuthorId()).isEqualTo("user-1");
        assertThat(result.getAuthorName()).isEqualTo("Demo User");
        assertThat(result.getContent()).isEqualTo("My first post");
        assertThat(result.getLikesCount()).isZero();
        assertThat(result.getCommentsCount()).isZero();

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());

        assertThat(captor.getValue().getAuthorHeadline())
                .isEqualTo("LinkedIn Member");
    }

    @Test
    void shouldGetPostById() {
        when(postRepository.findById(1L))
                .thenReturn(Optional.of(samplePost()));

        FeedPostResponse result = feedService.getPost(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("Hello LinkedIn");
    }

    @Test
    void shouldThrowWhenPostNotFound() {
        when(postRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> feedService.getPost(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post not found");
    }

    @Test
    void shouldDeleteOwnPost() {
        Post post = samplePost();

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        feedService.deletePost(1L, "user-1");

        verify(commentRepository).deleteByPostId(1L);
        verify(postLikeRepository).deleteByPostId(1L);
        verify(postRepository).delete(post);
    }

    @Test
    void shouldNotDeleteAnotherUsersPost() {
        Post post = samplePost();

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        assertThatThrownBy(() -> feedService.deletePost(1L, "user-2"))
                .isInstanceOf(ForbiddenActionException.class)
                .hasMessage("You can only delete your own post");

        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void shouldLikePostWhenNotAlreadyLiked() {
        Post post = samplePost();

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(postLikeRepository.existsByPostIdAndUserId(1L, "user-1"))
                .thenReturn(false);

        FeedPostResponse result = feedService.likePost(1L, "user-1");

        assertThat(result.getLikesCount()).isEqualTo(1);

        verify(postLikeRepository).save(any(PostLike.class));
        verify(postRepository).save(post);
    }

    @Test
    void shouldNotDuplicateLike() {
        Post post = samplePost();
        post.setLikesCount(1);

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(postLikeRepository.existsByPostIdAndUserId(1L, "user-1"))
                .thenReturn(true);

        FeedPostResponse result = feedService.likePost(1L, "user-1");

        assertThat(result.getLikesCount()).isEqualTo(1);

        verify(postLikeRepository, never()).save(any(PostLike.class));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void shouldUnlikePostWhenLiked() {
        Post post = samplePost();
        post.setLikesCount(1);

        PostLike like = PostLike.builder()
                .id(1L)
                .postId(1L)
                .userId("user-1")
                .createdAt(LocalDateTime.now())
                .build();

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(postLikeRepository.findByPostIdAndUserId(1L, "user-1"))
                .thenReturn(Optional.of(like));

        FeedPostResponse result = feedService.unlikePost(1L, "user-1");

        assertThat(result.getLikesCount()).isZero();

        verify(postLikeRepository).delete(like);
        verify(postRepository).save(post);
    }

    @Test
    void shouldReturnCommentsForPost() {
        when(postRepository.findById(1L))
                .thenReturn(Optional.of(samplePost()));

        Comment comment = sampleComment();

        when(commentRepository.findByPostIdOrderByCreatedAtAsc(1L))
                .thenReturn(List.of(comment));

        List<CommentResponse> result = feedService.getComments(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getContent()).isEqualTo("Nice post");
    }

    @Test
    void shouldAddComment() {
        Post post = samplePost();

        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("Nice post");

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment comment = invocation.getArgument(0);
                    comment.setId(1L);
                    return comment;
                });

        CommentResponse result =
                feedService.addComment(1L, "user-1", "Demo User", request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPostId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("Nice post");

        assertThat(post.getCommentsCount()).isEqualTo(1);

        verify(postRepository).save(post);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void shouldDeleteOwnComment() {
        Post post = samplePost();
        post.setCommentsCount(1);

        Comment comment = sampleComment();

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        when(postRepository.findById(1L))
                .thenReturn(Optional.of(post));

        feedService.deleteComment(1L, "user-1");

        assertThat(post.getCommentsCount()).isZero();

        verify(postRepository).save(post);
        verify(commentRepository).delete(comment);
    }

    @Test
    void shouldNotDeleteAnotherUsersComment() {
        Comment comment = sampleComment();

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> feedService.deleteComment(1L, "user-2"))
                .isInstanceOf(ForbiddenActionException.class)
                .hasMessage("You can only delete your own comment");

        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void shouldThrowWhenCommentNotFound() {
        when(commentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> feedService.deleteComment(99L, "user-1"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Comment not found");
    }

    private Post samplePost() {
        return Post.builder()
                .id(1L)
                .authorId("user-1")
                .authorName("Demo User")
                .authorHeadline("LinkedIn Member")
                .content("Hello LinkedIn")
                .likesCount(0)
                .commentsCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Comment sampleComment() {
        return Comment.builder()
                .id(1L)
                .postId(1L)
                .authorId("user-1")
                .authorName("Demo User")
                .content("Nice post")
                .createdAt(LocalDateTime.now())
                .build();
    }
}