package com.itclinkedin.userprofile.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class SkillResponse {

    private UUID id;

    private String skillName;

    private Integer endorsementCount;
}