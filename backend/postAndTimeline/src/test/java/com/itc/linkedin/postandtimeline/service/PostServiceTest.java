package com.itc.linkedin.postandtimeline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itc.linkedin.postandtimeline.dto.request.CreatePostRequest;
import com.itc.linkedin.postandtimeline.dto.response.PostResponse;
import com.itc.linkedin.postandtimeline.entity.Post;
import com.itc.linkedin.postandtimeline.kafka.event.PostCreatedEvent;
import com.itc.linkedin.postandtimeline.outbox.OutboxEvent;
import com.itc.linkedin.postandtimeline.outbox.OutboxEventRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    @Captor
    private ArgumentCaptor<OutboxEvent> outboxCaptor;

    private ObjectMapper objectMapper;

    private PostService postService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        postService = new PostService(postRepository, outboxEventRepository, objectMapper);
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
                new CreatePostRequest("Kafka outbox test post")
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
    }
}
