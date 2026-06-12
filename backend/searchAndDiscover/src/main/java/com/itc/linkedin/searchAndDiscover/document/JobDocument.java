package com.itc.linkedin.searchAndDiscover.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDocument {
    @Id
    private String id;
    private String title;
    private String companyName;
    private String location;
    private String workplaceType;
}