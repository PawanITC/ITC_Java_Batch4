package com.itclinkedin.userprofile.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "languages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String languageName;

    private String proficiency;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private UserProfile userProfile;
}