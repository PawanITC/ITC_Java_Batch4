package com.itclinkedin.jobPosting.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "job_requirements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "job_id", referencedColumnName = "id"),
            @JoinColumn(name = "company_id", referencedColumnName = "company_id")
    })
    private JobPost jobPost;

    @Column(nullable = false, length = 500)
    private String requirement;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory;
}