package com.itclinkedin.userprofile.dto.response;

import lombok.Data;

import java.util.UUID;
@Data
public class EducationResponse {

    private UUID id;
    private String schoolName;
    private String degree;
    private String fieldOfStudy;
    private Integer startYear;
    private Integer endYear;

}