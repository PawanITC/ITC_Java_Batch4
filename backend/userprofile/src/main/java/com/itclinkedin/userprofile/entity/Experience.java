
package com.itclinkedin.userprofile.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "experiences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String companyName;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean current;

    @Column(length = 3000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private UserProfile userProfile;
}