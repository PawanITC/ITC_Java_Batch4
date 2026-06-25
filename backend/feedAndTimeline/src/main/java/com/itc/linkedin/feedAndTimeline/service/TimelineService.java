package com.itc.linkedin.feedAndTimeline.service;

import com.itc.linkedin.feedAndTimeline.dto.response.TimelinePostResponse;
import com.itc.linkedin.feedAndTimeline.entity.FollowEdge;
import com.itc.linkedin.feedAndTimeline.entity.TimelinePost;
import com.itc.linkedin.feedAndTimeline.kafka.event.CommentCreatedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostCreatedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostDeletedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostLikedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.UserFollowedEvent;
import com.itc.linkedin.feedAndTimeline.repository.FollowEdgeRepository;
import com.itc.linkedin.feedAndTimeline.repository.TimelinePostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimelineService {

    private static final int FEED_PAGE_SIZE = 50;

    private final TimelinePostRepository timelinePostRepository;
    private final FollowEdgeRepository followEdgeRepository;

    @Cacheable(value = "timeline", key = "#userId")
    public List<TimelinePostResponse> getTimeline(String userId) {
        return getTimeline(userId, TimelineSortMode.TOP);
    }

    @Cacheable(value = "timeline", key = "#userId + ':' + #sortMode.name()")
    public List<TimelinePostResponse> getTimeline(String userId, TimelineSortMode sortMode) {
        log.info("CACHE MISS: Loading timeline from DB for userId={}", userId);

        if (sortMode == TimelineSortMode.RECENT) {
            return timelinePostRepository.findTop50ByTimelineUserIdOrderByCreatedAtDesc(userId)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
        }

        List<TimelinePost> candidates = timelinePostRepository.findTop200ByTimelineUserIdOrderByCreatedAtDesc(userId);
        Map<String, FollowEdge> followEdgesByAuthor = loadFollowEdgesByAuthor(userId, candidates);

        List<TimelinePost> rankedPosts = rankTimelinePosts(userId, candidates, followEdgesByAuthor);
        return rankedPosts.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "timeline", allEntries = true)
    public void handlePostCreated(PostCreatedEvent event) {
        log.info("Handling post.created event: postId={}, authorId={}", event.postId(), event.authorId());

        List<String> recipientIds = new ArrayList<>();
        recipientIds.add(event.authorId());
        recipientIds.addAll(followEdgeRepository.findFollowerIdsByFolloweeId(event.authorId()));

        int insertedCount = 0;
        for (String recipientId : recipientIds.stream().filter(Objects::nonNull).distinct().toList()) {
            insertedCount += insertTimelinePostIfMissing(recipientId, event) ? 1 : 0;
        }

        log.info("Fanout complete for postId={} authorId={} recipientsInserted={}",
                event.postId(), event.authorId(), insertedCount);
    }

    @Transactional
    @CacheEvict(value = "timeline", allEntries = true)
    public void handleUserFollowed(UserFollowedEvent event) {
        log.info("Handling social-follow-events: followerId={}, followingId={}", event.followerId(), event.followingId());

        if (event.followerId() == null || event.followingId() == null || event.followerId().equals(event.followingId())) {
            log.info("Ignoring invalid follow event followerId={} followingId={}", event.followerId(), event.followingId());
            return;
        }

        if (!followEdgeRepository.existsByFollowerIdAndFolloweeId(event.followerId(), event.followingId())) {
            followEdgeRepository.save(FollowEdge.builder()
                    .followerId(event.followerId())
                    .followeeId(event.followingId())
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        List<TimelinePost> recentPosts = timelinePostRepository
                .findTop50ByTimelineUserIdAndAuthorIdOrderByCreatedAtDesc(event.followingId(), event.followingId());

        int backfilledCount = 0;
        for (TimelinePost recentPost : recentPosts) {
            backfilledCount += insertTimelinePostIfMissing(event.followerId(), recentPost) ? 1 : 0;
        }

        log.info("Follow backfill complete followerId={} followingId={} insertedPosts={}",
                event.followerId(), event.followingId(), backfilledCount);
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
                .authorAvatarUrl(post.getAuthorAvatarUrl())
                .content(post.getContent())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private List<TimelinePost> rankTimelinePosts(
            String userId,
            List<TimelinePost> candidates,
            Map<String, FollowEdge> followEdgesByAuthor
    ) {
        List<ScoredTimelinePost> scoredPosts = candidates.stream()
                .map(post -> new ScoredTimelinePost(
                        post,
                        scorePost(userId, post, followEdgesByAuthor.get(post.getAuthorId()))
                ))
                .sorted(Comparator
                        .comparingInt(ScoredTimelinePost::score).reversed()
                        .thenComparing(scored -> scored.post().getCreatedAt(), Comparator.reverseOrder()))
                .toList();

        List<TimelinePost> ranked = new ArrayList<>();
        Map<String, Integer> authorCounts = new HashMap<>();

        for (ScoredTimelinePost scoredPost : scoredPosts) {
            int repeats = authorCounts.getOrDefault(scoredPost.post().getAuthorId(), 0);
            int repeatPenalty = repeats * 12;
            if (scoredPost.score() - repeatPenalty <= 0) {
                continue;
            }

            ranked.add(scoredPost.post());
            authorCounts.merge(scoredPost.post().getAuthorId(), 1, Integer::sum);

            if (ranked.size() >= FEED_PAGE_SIZE) {
                break;
            }
        }

        return ranked;
    }

    private Map<String, FollowEdge> loadFollowEdgesByAuthor(String userId, List<TimelinePost> candidates) {
        Set<String> authorIds = new HashSet<>();
        for (TimelinePost candidate : candidates) {
            if (candidate.getAuthorId() != null && !candidate.getAuthorId().equals(userId)) {
                authorIds.add(candidate.getAuthorId());
            }
        }

        if (authorIds.isEmpty()) {
            return Map.of();
        }

        return followEdgeRepository.findByFollowerIdAndFolloweeIdIn(userId, authorIds).stream()
                .collect(java.util.stream.Collectors.toMap(FollowEdge::getFolloweeId, edge -> edge, (left, right) -> left));
    }

    private int scorePost(String userId, TimelinePost post, FollowEdge followEdge) {
        int score = 0;

        score += scoreFreshness(post.getCreatedAt());
        score += scoreEngagement(post.getLikesCount(), post.getCommentsCount());
        score += scoreRelationship(userId, post, followEdge);
        score += scoreContentQuality(post.getContent());

        return score;
    }

    private int scoreFreshness(LocalDateTime createdAt) {
        if (createdAt == null) {
            return 0;
        }

        long minutesOld = Math.max(0, java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes());
        if (minutesOld <= 60) {
            return 140;
        }
        if (minutesOld <= 6 * 60) {
            return 110;
        }
        if (minutesOld <= 24 * 60) {
            return 80;
        }
        if (minutesOld <= 72 * 60) {
            return 45;
        }
        return 15;
    }

    private int scoreEngagement(int likesCount, int commentsCount) {
        int weightedEngagement = (likesCount * 3) + (commentsCount * 5);
        return Math.min(weightedEngagement, 120);
    }

    private int scoreRelationship(String userId, TimelinePost post, FollowEdge followEdge) {
        if (userId.equals(post.getAuthorId())) {
            return 50;
        }
        if (followEdge == null) {
            return 0;
        }

        long daysFollowing = Math.max(0, java.time.Duration.between(followEdge.getCreatedAt(), LocalDateTime.now()).toDays());
        if (daysFollowing <= 7) {
            return 50;
        }
        if (daysFollowing <= 30) {
            return 35;
        }
        return 20;
    }

    private int scoreContentQuality(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }

        int length = content.trim().length();
        if (length >= 400) {
            return 16;
        }
        if (length >= 140) {
            return 12;
        }
        if (length >= 40) {
            return 8;
        }
        return 4;
    }

    private boolean insertTimelinePostIfMissing(String timelineUserId, PostCreatedEvent event) {
        if (timelinePostRepository.findByTimelineUserIdAndPostId(timelineUserId, event.postId()).isPresent()) {
            return false;
        }

        TimelinePost saved = timelinePostRepository.save(TimelinePost.builder()
                .timelineUserId(timelineUserId)
                .postId(event.postId())
                .authorId(event.authorId())
                .authorName(event.authorName())
                .authorHeadline(event.authorHeadline())
                .authorAvatarUrl(null)
                .content(event.content())
                .likesCount(0)
                .commentsCount(0)
                .createdAt(event.createdAt())
                .updatedAt(LocalDateTime.now())
                .build());

        log.info("Saved timeline post successfully. id={}, postId={}, timelineUserId={}",
                saved.getId(), saved.getPostId(), timelineUserId);
        return true;
    }

    private boolean insertTimelinePostIfMissing(String timelineUserId, TimelinePost sourcePost) {
        if (timelinePostRepository.findByTimelineUserIdAndPostId(timelineUserId, sourcePost.getPostId()).isPresent()) {
            return false;
        }

        TimelinePost saved = timelinePostRepository.save(TimelinePost.builder()
                .timelineUserId(timelineUserId)
                .postId(sourcePost.getPostId())
                .authorId(sourcePost.getAuthorId())
                .authorName(sourcePost.getAuthorName())
                .authorHeadline(sourcePost.getAuthorHeadline())
                .authorAvatarUrl(sourcePost.getAuthorAvatarUrl())
                .content(sourcePost.getContent())
                .likesCount(sourcePost.getLikesCount())
                .commentsCount(sourcePost.getCommentsCount())
                .createdAt(sourcePost.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build());

        log.info("Backfilled timeline post successfully. id={}, postId={}, timelineUserId={}",
                saved.getId(), saved.getPostId(), timelineUserId);
        return true;
    }

    private record ScoredTimelinePost(TimelinePost post, int score) {
    }
}
