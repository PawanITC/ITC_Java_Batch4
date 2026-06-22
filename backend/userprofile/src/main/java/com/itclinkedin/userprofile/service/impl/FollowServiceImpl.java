package com.itclinkedin.userprofile.service.impl;

import com.itclinkedin.userprofile.dto.request.FollowRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;
import com.itclinkedin.userprofile.entity.Follow;
import com.itclinkedin.userprofile.entity.FollowId;
import com.itclinkedin.userprofile.entity.UserProfile;
import com.itclinkedin.userprofile.events.UserFollowedEvent;
import com.itclinkedin.userprofile.mapper.ProfileMapper;
import com.itclinkedin.userprofile.repository.FollowRepository;
import com.itclinkedin.userprofile.repository.UserProfileRepository;
import com.itclinkedin.userprofile.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserProfileRepository userProfileRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProfileMapper profileMapper;

    private static final String TOPIC = "social-follow-events";

    @Override
    @Transactional
    public void followUser(FollowRequest request) {
        if (request.getFollowerId().equals(request.getFollowingId())) {
            throw new IllegalArgumentException("Users cannot follow themselves");
        }

        FollowId followId = new FollowId(request.getFollowerId(), request.getFollowingId());
        if (followRepository.existsById(followId)) {
            throw new IllegalStateException("Already following this user");
        }

        UserProfile follower = userProfileRepository.findById(request.getFollowerId())
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        UserProfile following = userProfileRepository.findById(request.getFollowingId())
                .orElseThrow(() -> new RuntimeException("Target profile not found"));

        Follow follow = Follow.builder()
                .id(followId)
                .follower(follower)
                .following(following)
                .build();

        // 1. Write targets natively to PostgreSQL. Postgres calculates Hash and assigns to correct Shard
        followRepository.save(follow);

        // 2. Stream event out to Kafka instantly
        UserFollowedEvent event = new UserFollowedEvent(
                UUID.randomUUID().toString(),
                follower.getId().toString(),
                following.getId().toString(),
                follower.getFirstName(),
                follower.getLastName(),
                follower.getEmail(),
                following.getFirstName(),
                following.getLastName(),
                following.getEmail(),
                LocalDateTime.now().toString()
        );

        // Keying by followerId ensures predictable, sequential distribution inside Kafka partitions
        kafkaTemplate.send(TOPIC, follower.getId().toString(), event);
    }

    @Override
    @Transactional
    public void unfollowUser(FollowRequest request) {
        FollowId followId = new FollowId(request.getFollowerId(), request.getFollowingId());
        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new RuntimeException("Active follow relationship not found"));

        followRepository.delete(follow);
    }

    @Override
    @Transactional(readOnly = true)
    public long getFollowerCount(UUID profileId) {
        return followRepository.countById_FollowingId(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getFollowingCount(UUID profileId) {
        return followRepository.countById_FollowerId(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ProfileResponse> getFollowers(UUID profileId, Pageable pageable) {
        if (!userProfileRepository.existsById(profileId)) {
            throw new RuntimeException("Profile not found");
        }

        return followRepository.findById_FollowingId(profileId, pageable)
                .map(Follow::getFollower)
                .map(profileMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ProfileResponse> getFollowing(UUID profileId, Pageable pageable) {
        if (!userProfileRepository.existsById(profileId)) {
            throw new RuntimeException("Profile not found");
        }

        return followRepository.findById_FollowerId(profileId, pageable)
                .map(Follow::getFollowing)
                .map(profileMapper::toResponse);
    }
}