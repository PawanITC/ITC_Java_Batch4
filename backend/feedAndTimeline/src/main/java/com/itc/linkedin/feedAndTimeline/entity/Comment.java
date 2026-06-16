package com.itc.linkedin.feedAndTimeline.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private String authorId;

    private String authorName;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;
}
