package com.itclinkedin.jobPosting.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "job_benefits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "job_id", referencedColumnName = "id"),
            @JoinColumn(name = "company_id", referencedColumnName = "company_id")
    })
    private JobPost jobPost;

    @Column(nullable = false)
    private String benefit;
}