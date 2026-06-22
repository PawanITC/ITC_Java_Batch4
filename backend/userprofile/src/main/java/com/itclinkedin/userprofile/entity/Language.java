package com.itclinkedin.userprofile.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Language name cannot be empty")
    @Size(min = 2, max = 50, message = "Language name must be between 2 and 50 characters")
    private String languageName;

    @NotBlank(message = "Proficiency level is required")
    @Size(max = 50, message = "Proficiency description must be under 50 characters")
    private String proficiency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    @NotNull(message = "A language record must be associated with a valid user profile")
    private UserProfile userProfile;
}