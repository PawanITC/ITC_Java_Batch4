package com.itclinkedin.userprofile.repository;

import com.itclinkedin.userprofile.entity.Follow;
import com.itclinkedin.userprofile.entity.FollowId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    // Memory-safe slice lookups mapping across our sharded architecture
    Slice<Follow> findById_FollowingId(UUID followingId, Pageable pageable);
    Slice<Follow> findById_FollowerId(UUID followerId, Pageable pageable);

    long countById_FollowingId(UUID followingId);
    long countById_FollowerId(UUID followerId);
}