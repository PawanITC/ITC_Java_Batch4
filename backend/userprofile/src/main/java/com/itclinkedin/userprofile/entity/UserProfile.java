package com.itclinkedin.userprofile.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String firstName;
    private String lastName;
    private String email;
    private String headline;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 5000)
    private String about;

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

    private Boolean openToWork = false;
    private Boolean profilePublic = true;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    private List<Language> languages = new ArrayList<>();
}