package com.itclinkedin.userprofile.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FollowId implements Serializable {

    @NotNull(message = "The follower UUID key cannot be null")
    private UUID followerId;

    @NotNull(message = "The following target UUID key cannot be null")
    private UUID followingId;
}