package com.itclinkedin.userprofile.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateLanguageRequest {

    private UUID profileId;

    private String languageName;

    private String proficiency;
}