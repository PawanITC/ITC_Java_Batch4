package com.itc.linkedin.feedAndTimeline.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedPostResponse {

    private Long id;
    private String authorId;
    private String authorName;
    private String authorHeadline;
    private String content;
    private int likesCount;
    private int commentsCount;
    private LocalDateTime createdAt;
}