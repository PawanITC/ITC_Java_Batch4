package com.itc.linkedin.searchAndDiscover.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDocument {
    @Id
    private String id;
    private String authorName;
    private String content;
    private int likes;
    private int comments;
}