package com.itclinkedin.userprofile.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String skillName;

    private Integer endorsementCount;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private UserProfile userProfile;
}