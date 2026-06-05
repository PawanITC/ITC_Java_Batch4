package com.itclinkedin.userprofile.dto.request;

import lombok.Data;

@Data
public class CreateProfileRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String headline;
    private String about;
    private String city;
    private String country;
}