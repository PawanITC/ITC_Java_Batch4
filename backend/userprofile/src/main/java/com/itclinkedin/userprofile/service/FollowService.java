package com.itclinkedin.userprofile.service;

import com.itclinkedin.userprofile.dto.request.FollowRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import java.util.UUID;

public interface FollowService {
    void followUser(FollowRequest request);
    void unfollowUser(FollowRequest request);
    long getFollowerCount(UUID profileId);
    long getFollowingCount(UUID profileId);

    // 👇 Change to return ProfileResponse
    Slice<ProfileResponse> getFollowers(UUID profileId, Pageable pageable);
    Slice<ProfileResponse> getFollowing(UUID profileId, Pageable pageable);
}