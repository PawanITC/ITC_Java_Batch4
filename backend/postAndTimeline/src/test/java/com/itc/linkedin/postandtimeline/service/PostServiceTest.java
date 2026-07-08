package com.itc.linkedin.postandtimeline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itc.linkedin.postandtimeline.dto.request.CreatePostRequest;
import com.itc.linkedin.postandtimeline.dto.request.CreateCommentRequest;
import com.itc.linkedin.postandtimeline.dto.response.PostResponse;
import com.itc.linkedin.postandtimeline.entity.Comment;
import com.itc.linkedin.postandtimeline.entity.Post;
import com.itc.linkedin.postandtimeline.entity.PostLike;
import com.itc.linkedin.postandtimeline.kafka.event.CommentCreatedEvent;
import com.itc.linkedin.postandtimeline.kafka.event.PostCreatedEvent;
import com.itc.linkedin.postandtimeline.kafka.event.PostDeletedEvent;
import com.itc.linkedin.postandtimeline.kafka.event.PostLikedEvent;
import com.itc.linkedin.postandtimeline.outbox.OutboxEvent;
import com.itc.linkedin.postandtimeline.outbox.OutboxEventRepository;
import com.itc.linkedin.postandtimeline.repository.CommentRepository;
import com.itc.linkedin.postandtimeline.repository.PostLikeRepository;
import com.itc.linkedin.postandtimeline.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostMediaService postMediaService;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    @Captor
    private ArgumentCaptor<OutboxEvent> outboxCaptor;

    private ObjectMapper objectMapper;

    private PostService postService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        postService = new PostService(
                postRepository,
                postLikeRepository,
                commentRepository,
                outboxEventRepository,
                objectMapper,
                postMediaService
        );
    }

    @Test
    void shouldSavePostAndVersionedOutboxEventInSameCall() throws Exception {
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 23, 18, 0);

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(101L);
            post.setCreatedAt(createdAt);
            post.setUpdatedAt(createdAt);
            return post;
        });

        PostResponse response = postService.createPost(
                "user.demo",
                "user.demo",
                new CreatePostRequest("Kafka outbox test post", null, null)
        );

        verify(postRepository).save(postCaptor.capture());
        verify(outboxEventRepository).save(outboxCaptor.capture());

        Post savedPost = postCaptor.getValue();
        OutboxEvent outboxEvent = outboxCaptor.getValue();
        PostCreatedEvent event = objectMapper.readValue(outboxEvent.getPayload(), PostCreatedEvent.class);

        assertThat(response.id()).isEqualTo(101L);
        assertThat(savedPost.getAuthorId()).isEqualTo("user.demo");
        assertThat(savedPost.getAuthorName()).isEqualTo("user.demo");
        assertThat(savedPost.getContent()).isEqualTo("Kafka outbox test post");

        assertThat(outboxEvent.getAggregateType()).isEqualTo("POST");
        assertThat(outboxEvent.getAggregateId()).isEqualTo(101L);
        assertThat(outboxEvent.getEventType()).isEqualTo("post.created");
        assertThat(outboxEvent.getTopic()).isEqualTo("post.created");
        assertThat(outboxEvent.isPublished()).isFalse();

        assertThat(event.eventId()).isNotBlank();
        assertThat(event.eventType()).isEqualTo("post.created");
        assertThat(event.eventVersion()).isEqualTo(1);
        assertThat(event.postId()).isEqualTo(101L);
        assertThat(event.authorId()).isEqualTo("user.demo");
        assertThat(event.authorName()).isEqualTo("user.demo");
        assertThat(event.content()).isEqualTo("Kafka outbox test post");
        assertThat(event.mediaObjectKey()).isNull();
        assertThat(event.mediaType()).isNull();
    }

    @Test
    void shouldLikeAndUnlikePostAndPublishEvents() throws Exception {
        Post post = Post.builder()
                .id(11L)
                .authorId("author-1")
                .authorName("Author")
                .authorHeadline("Headline")
                .content("content")
                .likesCount(0)
                .commentsCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(postRepository.findById(11L)).thenReturn(Optional.of(post));
        when(postLikeRepository.existsByPostIdAndUserId(11L, "user-1")).thenReturn(false);
        when(postLikeRepository.countByPostId(11L)).thenReturn(1L, 0L);

        postService.likePost(11L, "user-1");
        postService.unlikePost(11L, "user-1");

        verify(postLikeRepository).save(any(PostLike.class));
        verify(postLikeRepository).deleteByPostIdAndUserId(11L, "user-1");

        ArgumentCaptor<OutboxEvent> outboxEvents = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository, times(2)).save(outboxEvents.capture());

        PostLikedEvent likeEvent = objectMapper.readValue(
                outboxEvents.getAllValues().get(0).getPayload(),
                PostLikedEvent.class
        );
        PostLikedEvent unlikeEvent = objectMapper.readValue(
                outboxEvents.getAllValues().get(1).getPayload(),
                PostLikedEvent.class
        );

        assertThat(likeEvent.likesCount()).isEqualTo(1);
        assertThat(unlikeEvent.likesCount()).isEqualTo(0);
    }

    @Test
    void shouldAddCommentAndPublishEvent() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 6, 25, 18, 30);
        Post post = Post.builder()
                .id(12L)
                .authorId("author-1")
                .authorName("Author")
                .authorHeadline("Headline")
                .content("content")
                .likesCount(0)
                .commentsCount(0)
                .createdAt(now.minusHours(1))
                .updatedAt(now.minusHours(1))
                .build();

        when(postRepository.findById(12L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(55L);
            comment.setCreatedAt(now);
            return comment;
        });
        when(commentRepository.countByPostId(12L)).thenReturn(1L);

        PostResponse response = postService.addComment(
                12L,
                "user-1",
                "User One",
                new CreateCommentRequest("Nice post")
        );

        assertThat(response.commentsCount()).isEqualTo(1);

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());

        CommentCreatedEvent event = objectMapper.readValue(
                outboxCaptor.getValue().getPayload(),
                CommentCreatedEvent.class
        );

        assertThat(event.commentId()).isEqualTo(55L);
        assertThat(event.postId()).isEqualTo(12L);
        assertThat(event.authorName()).isEqualTo("User One");
        assertThat(event.commentsCount()).isEqualTo(1);
    }

    @Test
    void shouldDeletePostAndPublishDeleteEvent() throws Exception {
        Post post = Post.builder()
                .id(13L)
                .authorId("author-1")
                .authorName("Author")
                .authorHeadline("Headline")
                .content("content")
                .build();

        when(postRepository.findById(13L)).thenReturn(Optional.of(post));

        postService.deletePost(13L, "author-1");

        verify(postLikeRepository).deleteByPostId(13L);
        verify(commentRepository).deleteByPostId(13L);
        verify(postRepository).delete(post);

        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());

        PostDeletedEvent event = objectMapper.readValue(
                outboxCaptor.getValue().getPayload(),
                PostDeletedEvent.class
        );

        assertThat(event.postId()).isEqualTo(13L);
        assertThat(event.authorId()).isEqualTo("author-1");
    }
}
