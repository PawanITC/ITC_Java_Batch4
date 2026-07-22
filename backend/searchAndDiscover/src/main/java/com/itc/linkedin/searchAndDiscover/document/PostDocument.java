package com.itc.linkedin.searchAndDiscover.document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDocument {
    private String id;
    private String authorName;
    private String content;
    private int likes;
    private int comments;
}
