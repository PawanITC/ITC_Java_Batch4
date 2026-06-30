package com.itc.linkedin.searchAndDiscover.document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDocument {
    private String id;
    private String title;
    private String companyName;
    private String location;
    private String workplaceType;
}
