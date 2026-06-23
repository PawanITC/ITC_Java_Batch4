package com.itclinkedin.userprofile.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Skill name cannot be empty")
    @Size(min = 1, max = 100, message = "Skill name must be between 1 and 100 characters")
    private String skillName;

    @Min(value = 0, message = "Endorsement count cannot be negative")
    private Integer endorsementCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @NotNull(message = "A skill must be linked to a user profile")
    private UserProfile userProfile;
}