package com.itc.linkedin.feedAndTimeline.repository;

import com.itc.linkedin.feedAndTimeline.entity.TimelinePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimelinePostRepository extends JpaRepository<TimelinePost, Long> {

    List<TimelinePost> findByTimelineUserIdOrderByCreatedAtDesc(String timelineUserId);

    List<TimelinePost> findTop200ByTimelineUserIdOrderByCreatedAtDesc(String timelineUserId);

    List<TimelinePost> findTop50ByTimelineUserIdOrderByCreatedAtDesc(String timelineUserId);

    Optional<TimelinePost> findByTimelineUserIdAndPostId(String timelineUserId, Long postId);

    List<TimelinePost> findByPostId(Long postId);

    List<TimelinePost> findTop50ByTimelineUserIdAndAuthorIdOrderByCreatedAtDesc(String timelineUserId, String authorId);

    void deleteByPostId(Long postId);

    void deleteByTimelineUserIdIn(List<String> timelineUserIds);
}
