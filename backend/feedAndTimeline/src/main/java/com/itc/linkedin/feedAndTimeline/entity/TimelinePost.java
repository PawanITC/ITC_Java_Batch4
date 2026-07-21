package com.itc.linkedin.feedAndTimeline.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "timeline_posts",
        indexes = {
                @Index(name = "idx_timeline_user_created", columnList = "timelineUserId, createdAt"),
                @Index(name = "idx_timeline_post_id", columnList = "postId")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_timeline_user_post",
                        columnNames = {"timelineUserId", "postId"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelinePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String timelineUserId;

    private Long postId;

    private String authorId;

    private String authorName;

    private String authorHeadline;

    private String authorAvatarUrl;

    @Column(length = 5000)
    private String content;

    @Column(length = 2048)
    private String mediaObjectKey;

    private String mediaType;

    private int likesCount;

    private int commentsCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
