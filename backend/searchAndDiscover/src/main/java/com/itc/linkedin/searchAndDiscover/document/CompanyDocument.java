package com.itc.linkedin.searchAndDiscover.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDocument {
    @Id
    private String id;
    private String name;
    private String industry;
    private String location;
    private int followers;
}