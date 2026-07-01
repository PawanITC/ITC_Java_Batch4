package com.itclinkedin.userprofile.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.itclinkedin.userprofile.entity.Gender;
import java.util.UUID;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private UUID id;
    private String keycloakUserId;
    private String firstName;
    private String lastName;
    private String email;
    private String headline;
    private String about;
    private Gender gender;
    private String city;
    private String country;
    private String profilePictureUrl;
    private String coverPhotoUrl;
    private String industry;
    private String currentCompany;
    private String currentPosition;
    private String website;
    private String githubUrl;
    private String linkedinUrl;
    private Boolean openToWork;
    private Boolean profilePublic;

    private List<EducationResponse> educations;
    private List<ExperienceResponse> experiences;
    private List<SkillResponse> skills;
    private List<LanguageResponse> languages;
}
