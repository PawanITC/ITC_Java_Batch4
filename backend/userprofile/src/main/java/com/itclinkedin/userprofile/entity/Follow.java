package com.itclinkedin.userprofile.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "follows", indexes = { @Index(name = "idx_follows_following_id", columnList = "following_id")
}
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Follow {

    @EmbeddedId
    @Valid
    @NotNull(message = "Composite follow ID key cannot be null")
    private FollowId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerId")
    @JoinColumn(name = "follower_id", nullable = false)
    @NotNull(message = "Follower source profile mapping is required")
    private UserProfile follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followingId")
    @JoinColumn(name = "following_id", nullable = false)
    @NotNull(message = "Following target profile mapping is required")
    private UserProfile following;

    @PastOrPresent(message = "Creation timestamp cannot be set in the future")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}