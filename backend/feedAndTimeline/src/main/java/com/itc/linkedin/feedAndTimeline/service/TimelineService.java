package com.itc.linkedin.feedAndTimeline.service;

import com.itc.linkedin.feedAndTimeline.dto.response.TimelinePostResponse;
import com.itc.linkedin.feedAndTimeline.entity.TimelinePost;
import com.itc.linkedin.feedAndTimeline.kafka.event.CommentCreatedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostCreatedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostDeletedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostLikedEvent;
import com.itc.linkedin.feedAndTimeline.repository.TimelinePostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineService {

    private final TimelinePostRepository timelinePostRepository;

    @Cacheable(value = "timeline", key = "#userId")
    public List<TimelinePostResponse> getTimeline(String userId) {
        log.info("CACHE MISS: Loading timeline from DB for userId={}", userId);

        return timelinePostRepository.findByTimelineUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "timeline", allEntries = true)
    public void handlePostCreated(PostCreatedEvent event) {
        log.info("Handling post.created event: postId={}, authorId={}", event.postId(), event.authorId());

        boolean alreadyExists = timelinePostRepository
                .findByTimelineUserIdAndPostId(event.authorId(), event.postId())
                .isPresent();

        if (alreadyExists) {
            log.info("Timeline post already exists. Skipping. postId={}, authorId={}", event.postId(), event.authorId());
            return;
        }

        TimelinePost timelinePost = TimelinePost.builder()
                .timelineUserId(event.authorId())
                .postId(event.postId())
                .authorId(event.authorId())
                .authorName(event.authorName())
                .authorHeadline(event.authorHeadline())
                .content(event.content())
                .likesCount(0)
                .commentsCount(0)
                .createdAt(event.createdAt())
                .updatedAt(LocalDateTime.now())
                .build();

        TimelinePost saved = timelinePostRepository.save(timelinePost);

        log.info("Saved timeline post successfully. id={}, postId={}", saved.getId(), saved.getPostId());
    }

    @Transactional
    @CacheEvict(value = "timeline", allEntries = true)
    public void handlePostDeleted(PostDeletedEvent event) {
        log.info("Handling post.deleted event: postId={}", event.postId());
        timelinePostRepository.deleteByPostId(event.postId());
    }

    @Transactional
    @CacheEvict(value = "timeline", allEntries = true)
    public void handlePostLiked(PostLikedEvent event) {
        log.info("Handling post.liked event: postId={}, likesCount={}", event.postId(), event.likesCount());

        List<TimelinePost> timelinePosts = timelinePostRepository.findByPostId(event.postId());

        timelinePosts.forEach(post -> {
            post.setLikesCount(event.likesCount());
            post.setUpdatedAt(LocalDateTime.now());
        });

        timelinePostRepository.saveAll(timelinePosts);
    }

    @Transactional
    @CacheEvict(value = "timeline", allEntries = true)
    public void handleCommentCreated(CommentCreatedEvent event) {
        log.info("Handling comment.created event: postId={}, commentsCount={}", event.postId(), event.commentsCount());

        List<TimelinePost> timelinePosts = timelinePostRepository.findByPostId(event.postId());

        timelinePosts.forEach(post -> {
            post.setCommentsCount(event.commentsCount());
            post.setUpdatedAt(LocalDateTime.now());
        });

        timelinePostRepository.saveAll(timelinePosts);
    }

    private TimelinePostResponse mapToResponse(TimelinePost post) {
        return TimelinePostResponse.builder()
                .postId(post.getPostId())
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