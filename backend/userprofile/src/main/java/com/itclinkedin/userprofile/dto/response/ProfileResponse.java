
package com.itclinkedin.userprofile.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProfileResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String headline;
    private String about;
    private String city;
    private String country;

    private List<EducationResponse> educations;
    private List<ExperienceResponse> experiences;
    private List<SkillResponse> skills;
    private List<LanguageResponse> languages;
}