package com.itc.linkedin.feedAndTimeline.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authorId;

    private String authorName;

    private String authorHeadline;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int likesCount;

    private int commentsCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
