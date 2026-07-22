package com.itclinkedin.userprofile.dto.request;

import lombok.Data;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
@Data
public class FollowRequest {

    @NotNull(message = "Follower ID is required")
    private UUID followerId;

    @NotNull(message = "Following ID is required")
    private UUID followingId;
}