
package com.itclinkedin.userprofile.entity;

import com.itclinkedin.userprofile.entity.Education;
import com.itclinkedin.userprofile.entity.Experience;
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

    @Column(length = 5000)
    private String about;

    private String city;
    private String country;
    private String profilePictureUrl;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL)
    private List<Language> languages = new ArrayList<>();

}