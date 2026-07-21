package com.itc.linkedin.feedAndTimeline.service;

import com.itc.linkedin.feedAndTimeline.entity.TimelinePost;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostCreatedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.UserFollowedEvent;
import com.itc.linkedin.feedAndTimeline.repository.FollowEdgeRepository;
import com.itc.linkedin.feedAndTimeline.repository.TimelinePostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimelineServiceTest {

    @Mock
    private TimelinePostRepository timelinePostRepository;

    @Mock
    private FollowEdgeRepository followEdgeRepository;

    @Mock
    private MediaUrlService mediaUrlService;

    @InjectMocks
    private TimelineService timelineService;

    @Test
    void shouldRankTimelineByFreshnessEngagementAndRelationship() {
        TimelinePost freshLowEngagement = TimelinePost.builder()
                .timelineUserId("user-1")
                .postId(1L)
                .authorId("author-1")
                .authorName("Author One")
                .content("fresh post")
                .likesCount(1)
                .commentsCount(0)
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .build();

        TimelinePost slightlyOlderHighEngagement = TimelinePost.builder()
                .timelineUserId("user-1")
                .postId(2L)
                .authorId("author-2")
                .authorName("Author Two")
                .content("older but much stronger post with discussion")
                .likesCount(15)
                .commentsCount(6)
                .createdAt(LocalDateTime.now().minusHours(3))
                .build();

        TimelinePost selfPost = TimelinePost.builder()
                .timelineUserId("user-1")
                .postId(3L)
                .authorId("user-1")
                .authorName("Self")
                .content("my own update")
                .likesCount(0)
                .commentsCount(0)
                .createdAt(LocalDateTime.now().minusHours(6))
                .build();

        when(timelinePostRepository.findTop200ByTimelineUserIdOrderByCreatedAtDesc("user-1"))
                .thenReturn(List.of(freshLowEngagement, slightlyOlderHighEngagement, selfPost));
        when(followEdgeRepository.findByFollowerIdAndFolloweeIdIn(eq("user-1"), any()))
                .thenReturn(List.of(
                        com.itc.linkedin.feedAndTimeline.entity.FollowEdge.builder()
                                .followerId("user-1")
                                .followeeId("author-1")
                                .createdAt(LocalDateTime.now().minusDays(2))
                                .build(),
                        com.itc.linkedin.feedAndTimeline.entity.FollowEdge.builder()
                                .followerId("user-1")
                                .followeeId("author-2")
                                .createdAt(LocalDateTime.now().minusDays(20))
                                .build()
                ));

        List<Long> rankedPostIds = timelineService.getTimeline("user-1").stream()
                .map(post -> post.postId())
                .toList();

        assertThat(rankedPostIds).containsExactly(2L, 1L, 3L);
    }

    @Test
    void shouldReturnRecentTimelineInReverseChronologicalOrder() {
        TimelinePost newest = TimelinePost.builder()
                .timelineUserId("user-1")
                .postId(20L)
                .authorId("author-1")
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .build();
        TimelinePost older = TimelinePost.builder()
                .timelineUserId("user-1")
                .postId(19L)
                .authorId("author-2")
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        when(timelinePostRepository.findTop50ByTimelineUserIdOrderByCreatedAtDesc("user-1"))
                .thenReturn(List.of(newest, older));

        List<Long> recentPostIds = timelineService.getTimeline("user-1", TimelineSortMode.RECENT).stream()
                .map(post -> post.postId())
                .toList();

        assertThat(recentPostIds).containsExactly(20L, 19L);
        verifyNoInteractions(followEdgeRepository);
    }

    @Test
    void shouldFanoutNewPostToAuthorAndFollowers() {
        PostCreatedEvent event = new PostCreatedEvent(
                "evt-1",
                "post.created",
                1,
                LocalDateTime.now(),
                10L,
                "author-1",
                "Author One",
                "Engineer",
                "hello network",
                null,
                null,
                LocalDateTime.now()
        );

        when(followEdgeRepository.findFollowerIdsByFolloweeId("author-1"))
                .thenReturn(List.of("follower-1", "follower-2"));
        when(timelinePostRepository.findByTimelineUserIdAndPostId(anyString(), eq(10L)))
                .thenReturn(Optional.empty());
        when(timelinePostRepository.save(any(TimelinePost.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        timelineService.handlePostCreated(event);

        ArgumentCaptor<TimelinePost> captor = ArgumentCaptor.forClass(TimelinePost.class);
        verify(timelinePostRepository, times(3)).save(captor.capture());

        List<String> recipients = captor.getAllValues().stream()
                .map(TimelinePost::getTimelineUserId)
                .toList();

        assertThat(recipients).containsExactlyInAnyOrder("author-1", "follower-1", "follower-2");
    }

    @Test
    void shouldBackfillRecentPostsWhenUserFollowsAuthor() {
        UserFollowedEvent event = new UserFollowedEvent(
                "follow-1",
                "follower-1",
                "author-1",
                "Follower",
                "One",
                "follower@example.com",
                "Author",
                "One",
                "author@example.com",
                "2026-06-25T12:00:00"
        );

        TimelinePost existingAuthorPost = TimelinePost.builder()
                .timelineUserId("author-1")
                .postId(11L)
                .authorId("author-1")
                .authorName("Author One")
                .authorHeadline("Engineer")
                .content("Older but relevant")
                .likesCount(4)
                .commentsCount(2)
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        when(followEdgeRepository.existsByFollowerIdAndFolloweeId("follower-1", "author-1"))
                .thenReturn(false);
        when(timelinePostRepository.findTop50ByTimelineUserIdAndAuthorIdOrderByCreatedAtDesc("author-1", "author-1"))
                .thenReturn(List.of(existingAuthorPost));
        when(timelinePostRepository.findByTimelineUserIdAndPostId("follower-1", 11L))
                .thenReturn(Optional.empty());
        when(timelinePostRepository.save(any(TimelinePost.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        timelineService.handleUserFollowed(event);

        verify(followEdgeRepository).save(any());

        ArgumentCaptor<TimelinePost> captor = ArgumentCaptor.forClass(TimelinePost.class);
        verify(timelinePostRepository).save(captor.capture());

        TimelinePost saved = captor.getValue();
        assertThat(saved.getTimelineUserId()).isEqualTo("follower-1");
        assertThat(saved.getAuthorId()).isEqualTo("author-1");
        assertThat(saved.getPostId()).isEqualTo(11L);
        assertThat(saved.getLikesCount()).isEqualTo(4);
        assertThat(saved.getCommentsCount()).isEqualTo(2);
    }
}
