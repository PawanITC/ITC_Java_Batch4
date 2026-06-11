package com.itclinkedin.userprofile.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;
@Data
public class ExperienceResponse {

    private UUID id;
    private String companyName;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean current;
    private String description;
}