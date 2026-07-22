package com.itclinkedin.jobPosting.dto.response;

import com.itclinkedin.jobPosting.constant.JobStatus;
import jdk.jshell.Snippet;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class JobPostResponse {
    private UUID id;
    private UUID companyId;
    private String title;
    private String description;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private JobStatus status;
    private List<JobRequirementResponse> requirements;
    private List<String> benefits;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}