package com.itc.linkedin.postandtimeline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itc.linkedin.postandtimeline.dto.request.CreatePostRequest;
import com.itc.linkedin.postandtimeline.dto.response.PostResponse;
import com.itc.linkedin.postandtimeline.entity.Post;
import com.itc.linkedin.postandtimeline.kafka.event.PostCreatedEvent;
import com.itc.linkedin.postandtimeline.outbox.OutboxEvent;
import com.itc.linkedin.postandtimeline.outbox.OutboxEventRepository;
import com.itc.linkedin.postandtimeline.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public PostResponse createPost(
            String userId,
            String username,
            CreatePostRequest request
    ) {
        Post post = Post.builder()
                .authorId(userId)
                .authorName(username)
                .authorHeadline("LinkedIn Member")
                .content(request.content())
                .likesCount(0)
                .commentsCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Post savedPost = postRepository.save(post);

        try {
            PostCreatedEvent event = new PostCreatedEvent(
                    UUID.randomUUID().toString(),
                    "post.created",
                    1,
                    LocalDateTime.now(),
                    savedPost.getId(),
                    savedPost.getAuthorId(),
                    savedPost.getAuthorName(),
                    savedPost.getAuthorHeadline(),
                    savedPost.getContent(),
                    savedPost.getCreatedAt()
            );

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType("POST")
                    .aggregateId(savedPost.getId())
                    .eventType("post.created")
                    .topic("post.created")
                    .payload(objectMapper.writeValueAsString(event))
                    .published(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(outboxEvent);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create outbox event", e);
        }

        return mapToResponse(savedPost);
    }

    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
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
}
