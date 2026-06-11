package com.itclinkedin.userprofile.dto.request;

import lombok.Data;

import java.util.UUID;
@Data
public class CreateEducationRequest {

    private UUID profileId;
    private String schoolName;
    private String degree;
    private String fieldOfStudy;
    private Integer startYear;
    private Integer endYear;

}