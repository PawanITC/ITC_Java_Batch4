package com.itclinkedin.userprofile.unit.service;

import com.itclinkedin.userprofile.dto.request.FollowRequest;
import com.itclinkedin.userprofile.entity.Follow;
import com.itclinkedin.userprofile.entity.FollowId;
import com.itclinkedin.userprofile.entity.UserProfile;
import com.itclinkedin.userprofile.mapper.ProfileMapper;
import com.itclinkedin.userprofile.repository.FollowRepository;
import com.itclinkedin.userprofile.repository.UserProfileRepository;
import com.itclinkedin.userprofile.service.impl.FollowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceImplTest {

    @Mock private FollowRepository followRepository;
    @Mock private UserProfileRepository userProfileRepository;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock private ProfileMapper profileMapper;

    @InjectMocks private FollowServiceImpl followService;

    private UUID followerId;
    private UUID followingId;
    private FollowRequest followRequest;
    private UserProfile follower;
    private UserProfile following;

    @BeforeEach
    void setUp() {
        followerId = UUID.randomUUID();
        followingId = UUID.randomUUID();

        followRequest = new FollowRequest();
        followRequest.setFollowerId(followerId);
        followRequest.setFollowingId(followingId);

        follower = UserProfile.builder().id(followerId).firstName("Hassan").lastName("Khan").email("hassan@gmail.com").build();
        following = UserProfile.builder().id(followingId).firstName("Ali").lastName("Khan").email("ali@gmail.com").build();
    }

    // ==========================================
    // FOLLOW USER PATHS
    // ==========================================

    @Test
    void followUser_Success() {
        FollowId followId = new FollowId(followerId, followingId);
        when(followRepository.existsById(followId)).thenReturn(false);
        when(userProfileRepository.findById(followerId)).thenReturn(Optional.of(follower));
        when(userProfileRepository.findById(followingId)).thenReturn(Optional.of(following));
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(mock(CompletableFuture.class));

        assertDoesNotThrow(() -> followService.followUser(followRequest));

        verify(followRepository, times(1)).save(any(Follow.class));
        verify(kafkaTemplate, times(1)).send(eq("social-follow-events"), eq(followerId.toString()), any());
    }

    @Test
    void followUser_Failure_SelfFollow() {
        FollowRequest selfRequest = new FollowRequest();
        selfRequest.setFollowerId(followerId);
        selfRequest.setFollowingId(followerId);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> followService.followUser(selfRequest));
        assertEquals("Users cannot follow themselves", ex.getMessage());
        verifyNoInteractions(followRepository, kafkaTemplate);
    }

    @Test
    void followUser_Failure_AlreadyFollowing() {
        when(followRepository.existsById(any(FollowId.class))).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> followService.followUser(followRequest));
        assertEquals("Already following this user", ex.getMessage());
        verify(followRepository, never()).save(any());
    }

    @Test
    void followUser_Failure_FollowerNotFound() {
        when(followRepository.existsById(any(FollowId.class))).thenReturn(false);
        when(userProfileRepository.findById(followerId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> followService.followUser(followRequest));
        assertEquals("Follower not found", ex.getMessage());
    }

    @Test
    void followUser_Failure_TargetNotFound() {
        when(followRepository.existsById(any(FollowId.class))).thenReturn(false);
        when(userProfileRepository.findById(followerId)).thenReturn(Optional.of(follower));
        when(userProfileRepository.findById(followingId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> followService.followUser(followRequest));
        assertEquals("Target profile not found", ex.getMessage());
    }

    // ==========================================
    // UNFOLLOW USER PATHS
    // ==========================================

    @Test
    void unfollowUser_Success() {
        Follow mockFollow = new Follow();
        when(followRepository.findById(any(FollowId.class))).thenReturn(Optional.of(mockFollow));

        assertDoesNotThrow(() -> followService.unfollowUser(followRequest));
        verify(followRepository, times(1)).delete(mockFollow);
    }

    @Test
    void unfollowUser_Failure_NotFound() {
        when(followRepository.findById(any(FollowId.class))).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> followService.unfollowUser(followRequest));
        assertEquals("Active follow relationship not found", ex.getMessage());
        verify(followRepository, never()).delete(any());
    }

    // ==========================================
    // READ QUERIES (Slices & Counts)
    // ==========================================

    @Test
    void getFollowers_Failure_ProfileNotFound() {
        UUID profileId = UUID.randomUUID();
        when(userProfileRepository.existsById(profileId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> followService.getFollowers(profileId, Pageable.unpaged()));
    }

    @Test
    void getFollowing_Failure_ProfileNotFound() {
        UUID profileId = UUID.randomUUID();
        when(userProfileRepository.existsById(profileId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> followService.getFollowing(profileId, Pageable.unpaged()));
    }
}