package com.itclinkedin.userprofile.controller;

import com.itclinkedin.userprofile.dto.request.FollowRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;
import com.itclinkedin.userprofile.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
@Tag(name = "Follow Infrastructure API", description = "Endpoints handling sharded follow records and event streaming.")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow")
    @Operation(summary = "Follow a user profile", description = "Saves record to native hash shards and emits real-time event to Kafka.")
    public ResponseEntity<Void> followUser(@Valid @RequestBody FollowRequest request) {
        followService.followUser(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unfollow")
    @Operation(summary = "Unfollow a user profile")
    public ResponseEntity<Void> unfollowUser(@Valid @RequestBody FollowRequest request) {
        followService.unfollowUser(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{profileId}/followers/count")
    public ResponseEntity<Long> getFollowerCount(@PathVariable UUID profileId) {
        return ResponseEntity.ok(followService.getFollowerCount(profileId));
    }

    @GetMapping("/{profileId}/following/count")
    public ResponseEntity<Long> getFollowingCount(@PathVariable UUID profileId) {
        return ResponseEntity.ok(followService.getFollowingCount(profileId));
    }

    @GetMapping("/{profileId}/followers")
    @Operation(summary = "Get slice of followers for a user", description = "Fetches a memory-safe, paginated list of followers from sharded storage without circular reference loops.")
    public ResponseEntity<Slice<ProfileResponse>> getFollowers(
            @PathVariable UUID profileId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowers(profileId, pageable));
    }

    @GetMapping("/{profileId}/following")
    @Operation(summary = "Get slice of profiles this user is following")
    public ResponseEntity<Slice<ProfileResponse>> getFollowing(
            @PathVariable UUID profileId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowing(profileId, pageable));
    }
}