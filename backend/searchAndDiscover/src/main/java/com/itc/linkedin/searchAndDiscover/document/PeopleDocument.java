package com.itc.linkedin.searchAndDiscover.document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeopleDocument {

    private String id;
    private String fullName;
    private String headline;
    private String location;
    private String skills;
}
