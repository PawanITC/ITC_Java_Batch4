package com.itc.linkedin.searchAndDiscover.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "people")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeopleDocument {

    @Id
    private String id;
    private String fullName;
    private String headline;
    private String location;
    private String skills;
}