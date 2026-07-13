package com.itclinkedin.userprofile.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itclinkedin.userprofile.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class CreateProfileRequest {

    @JsonIgnore
    private String keycloakUserId;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email format")
    private String email;

    @Size(max = 220, message = "Headline cannot exceed 220 characters")
    private String headline;

    @Size(max = 5000, message = "About summary cannot exceed 5000 characters")
    private String about;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private String city;

    private String country;

//    @URL(message = "Profile picture must be a valid URL link")
    private String profilePictureUrl;

//    @URL(message = "Cover photo must be a valid URL link")
    private String coverPhotoUrl;

    private String industry;
    private String currentCompany;
    private String currentPosition;

    @URL(message = "Website must be a valid URL")
    private String website;

    @URL(message = "GitHub profile must be a valid URL link")
    private String githubUrl;

    @URL(message = "LinkedIn profile must be a valid URL link")
    private String linkedinUrl;

    private Boolean openToWork;
    private Boolean profilePublic;
}
