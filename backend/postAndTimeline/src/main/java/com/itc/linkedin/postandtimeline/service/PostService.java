package com.itc.linkedin.postandtimeline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itc.linkedin.postandtimeline.dto.request.CreateCommentRequest;
import com.itc.linkedin.postandtimeline.dto.request.CreatePostRequest;
import com.itc.linkedin.postandtimeline.dto.response.CommentResponse;
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
import com.itc.linkedin.postandtimeline.repository.PostRepository;
import com.itc.linkedin.postandtimeline.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final PostMediaService postMediaService;

    @Transactional
    public PostResponse createPost(
            String userId,
            String username,
            CreatePostRequest request
    ) {
        String content = request.content() == null ? "" : request.content().trim();
        if (content.isBlank() && (request.mediaObjectKey() == null || request.mediaObjectKey().isBlank())) {
            throw new ResponseStatusException(BAD_REQUEST, "Post must include text, photo, or video.");
        }

        Post post = Post.builder()
                .authorId(userId)
                .authorName(username)
                .authorHeadline("LinkedIn Member")
                .authorAvatarUrl(null)
                .content(content)
                .mediaObjectKey(request.mediaObjectKey())
                .mediaType(normalizeMediaType(request.mediaType(), request.mediaObjectKey()))
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
                    savedPost.getMediaObjectKey(),
                    savedPost.getMediaType(),
                    savedPost.getCreatedAt()
            );

            saveOutboxEvent(savedPost.getId(), "post.created", "post.created", event);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create outbox event", e);
        }

        return mapToResponse(savedPost);
    }

    @Transactional
    public PostResponse likePost(Long postId, String userId) {
        Post post = getPostOrThrow(postId);

        if (!postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            postLikeRepository.save(PostLike.builder()
                    .postId(postId)
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        long likesCount = postLikeRepository.countByPostId(postId);
        post.setLikesCount((int) likesCount);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        publishPostLiked(postId, userId, (int) likesCount);
        return mapToResponse(post);
    }

    @Transactional
    public PostResponse unlikePost(Long postId, String userId) {
        Post post = getPostOrThrow(postId);

        postLikeRepository.deleteByPostIdAndUserId(postId, userId);

        long likesCount = postLikeRepository.countByPostId(postId);
        post.setLikesCount((int) likesCount);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        publishPostLiked(postId, userId, (int) likesCount);
        return mapToResponse(post);
    }

    @Transactional
    public PostResponse addComment(
            Long postId,
            String userId,
            String username,
            CreateCommentRequest request
    ) {
        Post post = getPostOrThrow(postId);

        Comment comment = commentRepository.save(Comment.builder()
                .postId(postId)
                .authorId(userId)
                .authorName(username)
                .content(request.content())
                .createdAt(LocalDateTime.now())
                .build());

        long commentsCount = commentRepository.countByPostId(postId);
        post.setCommentsCount((int) commentsCount);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        publishCommentCreated(postId, comment, (int) commentsCount);
        return mapToResponse(post);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {
        getPostOrThrow(postId);
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::mapCommentToResponse)
                .toList();
    }

    @Transactional
    public void deletePost(Long postId, String userId) {
        Post post = getPostOrThrow(postId);

        if (!post.getAuthorId().equals(userId)) {
            throw new ResponseStatusException(FORBIDDEN, "You can delete only your own posts.");
        }

        postLikeRepository.deleteByPostId(postId);
        commentRepository.deleteByPostId(postId);
        postRepository.delete(post);

        try {
            saveOutboxEvent(
                    postId,
                    "post.deleted",
                    "post.deleted",
                    new PostDeletedEvent(
                            nextEventId(),
                            postId,
                            userId,
                            LocalDateTime.now()
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create delete outbox event", e);
        }
    }

    private void publishPostLiked(Long postId, String userId, int likesCount) {
        try {
            saveOutboxEvent(
                    postId,
                    "post.liked",
                    "post.liked",
                    new PostLikedEvent(
                            nextEventId(),
                            postId,
                            post.getAuthorId(),
                            userId,
                            userId,
                            likesCount,
                            LocalDateTime.now()
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create like outbox event", e);
        }
    }

    private void publishCommentCreated(Long postId, Comment comment, int commentsCount) {
        try {
            saveOutboxEvent(
                    postId,
                    "comment.created",
                    "comment.created",
                    new CommentCreatedEvent(
                            nextEventId(),
                            comment.getId(),
                            postId,
                            post.getAuthorId(),
                            comment.getAuthorId(),
                            comment.getAuthorName(),
                            commentsCount,
                            comment.getCreatedAt()
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create comment outbox event", e);
        }
    }

    private void saveOutboxEvent(Long aggregateId, String eventType, String topic, Object payload) throws Exception {
        outboxEventRepository.save(OutboxEvent.builder()
                .aggregateType("POST")
                .aggregateId(aggregateId)
                .eventType(eventType)
                .topic(topic)
                .payload(objectMapper.writeValueAsString(payload))
                .published(false)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Post not found."));
    }

    private long nextEventId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }

    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .authorName(post.getAuthorName())
                .authorHeadline(post.getAuthorHeadline())
                .authorAvatarUrl(post.getAuthorAvatarUrl())
                .content(post.getContent())
                .mediaUrl(postMediaService.presignedUrl(post.getMediaObjectKey()))
                .mediaObjectKey(post.getMediaObjectKey())
                .mediaType(post.getMediaType())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private CommentResponse mapCommentToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .authorId(comment.getAuthorId())
                .authorName(comment.getAuthorName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private String normalizeMediaType(String mediaType, String mediaObjectKey) {
        if (mediaObjectKey == null || mediaObjectKey.isBlank()) {
            return null;
        }
        if (mediaType == null || mediaType.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "Media type is required when media URL is provided.");
        }

        String normalized = mediaType.trim().toUpperCase();
        if (!normalized.equals("IMAGE") && !normalized.equals("VIDEO")) {
            throw new ResponseStatusException(BAD_REQUEST, "Media type must be IMAGE or VIDEO.");
        }

        return normalized;
    }
}
