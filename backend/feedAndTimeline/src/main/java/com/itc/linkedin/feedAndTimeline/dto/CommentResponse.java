package com.itc.linkedin.feedAndTimeline.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private Long postId;
    private String authorId;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
}