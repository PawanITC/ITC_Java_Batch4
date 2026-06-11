package com.itclinkedin.userprofile.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class LanguageResponse {

    private UUID id;

    private String languageName;

    private String proficiency;
}