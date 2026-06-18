package com.itclinkedin.userprofile.dto.request;

import com.itclinkedin.userprofile.entity.Gender; // <--- Add this import statement
import lombok.Data;

@Data
public class CreateProfileRequest {

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
}