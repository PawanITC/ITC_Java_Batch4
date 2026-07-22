package com.itc.linkedin.feedAndTimeline.repository;

import com.itc.linkedin.feedAndTimeline.entity.FollowEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface FollowEdgeRepository extends JpaRepository<FollowEdge, Long> {

    boolean existsByFollowerIdAndFolloweeId(String followerId, String followeeId);

    @Query("select edge.followerId from FollowEdge edge where edge.followeeId = :followeeId")
    List<String> findFollowerIdsByFolloweeId(String followeeId);

    List<FollowEdge> findByFollowerIdAndFolloweeIdIn(String followerId, Collection<String> followeeIds);

    void deleteByFollowerId(String followerId);

    void deleteByFollowerIdAndFolloweeId(String followerId, String followeeId);
}
