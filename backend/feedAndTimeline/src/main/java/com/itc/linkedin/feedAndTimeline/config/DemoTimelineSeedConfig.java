package com.itc.linkedin.feedAndTimeline.config;

import com.itc.linkedin.feedAndTimeline.entity.FollowEdge;
import com.itc.linkedin.feedAndTimeline.entity.TimelinePost;
import com.itc.linkedin.feedAndTimeline.repository.FollowEdgeRepository;
import com.itc.linkedin.feedAndTimeline.repository.TimelinePostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DemoTimelineSeedConfig {

    private final TimelinePostRepository timelinePostRepository;
    private final FollowEdgeRepository followEdgeRepository;
    private final TransactionTemplate transactionTemplate;

    @Bean
    ApplicationRunner demoTimelineSeeder(
            @Value("${app.timeline.seed-demo-data:true}") boolean seedDemoData,
            @Value("${app.timeline.reset-demo-data:false}") boolean resetDemoData,
            @Value("${app.timeline.demo-user-id:b153813e-ab6d-4e49-8f01-df40055dfd88}") String demoUserId
    ) {
        return args -> {
            transactionTemplate.executeWithoutResult(status ->
                    seedDemoTimeline(seedDemoData, resetDemoData, demoUserId)
            );
        };
    }

    private void seedDemoTimeline(boolean seedDemoData, boolean resetDemoData, String demoUserId) {
        if (!seedDemoData) {
            return;
        }

        String authorOne = "7db89d14-1c88-4e7b-8d1a-9a7ff4d6f001";
        String authorTwo = "7db89d14-1c88-4e7b-8d1a-9a7ff4d6f002";
        String authorThree = "7db89d14-1c88-4e7b-8d1a-9a7ff4d6f003";
        String authorFour = "7db89d14-1c88-4e7b-8d1a-9a7ff4d6f004";

        if (resetDemoData) {
            log.info("Resetting demo timeline data for userId={}", demoUserId);
            timelinePostRepository.deleteByTimelineUserIdIn(List.of(
                    demoUserId,
                    authorOne,
                    authorTwo,
                    authorThree,
                    authorFour
            ));
            followEdgeRepository.deleteByFollowerId(demoUserId);
        }

        if (!timelinePostRepository.findTop50ByTimelineUserIdOrderByCreatedAtDesc(demoUserId).isEmpty()) {
            return;
        }

        log.info("Seeding demo timeline data for userId={}", demoUserId);

        LocalDateTime now = LocalDateTime.now();

        seedFollowEdge(demoUserId, authorOne, now.minusDays(2));
        seedFollowEdge(demoUserId, authorTwo, now.minusDays(14));
        seedFollowEdge(demoUserId, authorThree, now.minusDays(45));
        seedFollowEdge(demoUserId, authorFour, now.minusDays(6));

        List<SeedPost> seedPosts = List.of(
                new SeedPost(2001L, authorOne, "Aarav Mehta", "Staff Backend Engineer", "/avatars/aarav", "Shipped a cleaner Kafka outbox path for post publishing today. Feed fanout is quieter, retries are predictable, and the gateway metrics finally tell the truth.", 58, 12, now.minusMinutes(32)),
                new SeedPost(2002L, authorTwo, "Priya Nair", "Product Manager, Growth", "/avatars/priya", "A small profile-completion experiment beat the bigger redesign. The lesson is still the same: product clarity compounds faster than extra UI.", 91, 18, now.minusHours(2)),
                new SeedPost(2003L, authorThree, "Daniel Brooks", "Cloud Platform Engineer", "/avatars/daniel", "OpenSearch got much better once we stopped treating every field equally. Exact match, phrase relevance, and social proof should not share the same weight.", 36, 7, now.minusHours(6)),
                new SeedPost(2004L, authorFour, "Nina Patel", "Engineering Manager", "/avatars/nina", "Good feed ranking is not just freshness. Relationship strength, discussion quality, and author diversity all matter if you want the home page to feel alive.", 74, 15, now.minusHours(11)),
                new SeedPost(2005L, demoUserId, "Shubhra Tripathi", "Java Full Stack Developer | Spring Boot | React", "/avatars/shubhra", "Cleaned up the timeline demo data and tightened the auth boundary. Next target is making the search and feed feel more like a real product, not a sample app.", 29, 6, now.minusHours(4)),
                new SeedPost(2006L, authorTwo, "Priya Nair", "Product Manager, Growth", "/avatars/priya", "The best search result is usually the one that resolves intent fastest, not the one with the most text overlap. Ranking needs product judgment, not just token matching.", 47, 9, now.minusDays(1))
        );

        List<TimelinePost> rows = new ArrayList<>();
        for (SeedPost seedPost : seedPosts) {
            rows.add(toTimelineRow(seedPost.authorId(), seedPost));

            if (!seedPost.authorId().equals(demoUserId)) {
                rows.add(toTimelineRow(demoUserId, seedPost));
            }
        }

        timelinePostRepository.saveAll(rows);
    }

    private void seedFollowEdge(String followerId, String followeeId, LocalDateTime createdAt) {
        if (followEdgeRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            return;
        }

        followEdgeRepository.save(FollowEdge.builder()
                .followerId(followerId)
                .followeeId(followeeId)
                .createdAt(createdAt)
                .build());
    }

    private TimelinePost toTimelineRow(String timelineUserId, SeedPost seedPost) {
        return TimelinePost.builder()
                .timelineUserId(timelineUserId)
                .postId(seedPost.postId())
                .authorId(seedPost.authorId())
                .authorName(seedPost.authorName())
                .authorHeadline(seedPost.authorHeadline())
                .authorAvatarUrl(seedPost.authorAvatarUrl())
                .content(seedPost.content())
                .likesCount(seedPost.likesCount())
                .commentsCount(seedPost.commentsCount())
                .createdAt(seedPost.createdAt())
                .updatedAt(seedPost.createdAt())
                .build();
    }

    private record SeedPost(
            Long postId,
            String authorId,
            String authorName,
            String authorHeadline,
            String authorAvatarUrl,
            String content,
            int likesCount,
            int commentsCount,
            LocalDateTime createdAt
    ) {
    }
}
