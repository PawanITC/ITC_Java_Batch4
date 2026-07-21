package com.itclinkedin.userprofile.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

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

    @Column(name = "keycloak_user_id", unique = true, nullable = false)
    private String keycloakUserId;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @Size(max = 220, message = "Headline cannot exceed 220 characters")
    private String headline;

    @NotNull(message = "Gender specification is required")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Size(max = 5000, message = "About text summary cannot exceed 5000 characters")
    @Column(length = 5000)
    private String about;

    private String city;

    private String country;

    @URL(message = "Profile picture must be a valid URL link")
    private String profilePictureUrl;

    @URL(message = "Cover photo must be a valid URL link")
    private String coverPhotoUrl;

    private String industry;
    private String currentCompany;
    private String currentPosition;

    @URL(message = "Website must be a valid URL")
    private String website;

    @URL(message = "GitHub connection must be a valid URL link")
    private String githubUrl;

    @URL(message = "LinkedIn tracking handle must be a valid URL link")
    private String linkedinUrl;

    @Builder.Default
    private Boolean openToWork = false;

    @Builder.Default
    private Boolean profilePublic = true;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Language> languages = new ArrayList<>();
}
