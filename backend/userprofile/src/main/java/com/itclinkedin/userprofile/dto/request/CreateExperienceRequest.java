package com.itclinkedin.userprofile.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateExperienceRequest {

    private UUID profileId;

    private String companyName;
    private String title;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean current;
}