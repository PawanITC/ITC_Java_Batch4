package com.itc.linkedin.feedAndTimeline.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "follow_edges",
        indexes = {
                @Index(name = "idx_follow_edge_followee", columnList = "followeeId"),
                @Index(name = "idx_follow_edge_follower", columnList = "followerId")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_follow_edge_follower_followee",
                        columnNames = {"followerId", "followeeId"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String followerId;

    private String followeeId;

    private LocalDateTime createdAt;
}
