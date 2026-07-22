package com.itc.linkedin.postandtimeline.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
