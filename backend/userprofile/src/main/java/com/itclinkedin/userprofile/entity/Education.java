package com.itclinkedin.userprofile.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "educations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String schoolName;
    private String degree;
    private String fieldOfStudy;
    private Integer startYear;
    private Integer endYear;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private UserProfile userProfile;
}