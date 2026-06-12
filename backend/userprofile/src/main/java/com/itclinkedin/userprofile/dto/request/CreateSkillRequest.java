package com.itclinkedin.userprofile.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateSkillRequest {

    private UUID profileId;

    private String skillName;

    private Integer endorsementCount;
}