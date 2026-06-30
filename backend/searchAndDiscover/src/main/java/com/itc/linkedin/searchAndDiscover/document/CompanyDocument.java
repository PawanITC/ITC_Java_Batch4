package com.itc.linkedin.searchAndDiscover.document;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDocument {
    private String id;
    private String name;
    private String industry;
    private String location;
    private int followers;
}
