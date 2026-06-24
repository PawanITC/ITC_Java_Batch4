package com.itclinkedin.jobPosting.dto.request;

import com.itclinkedin.jobPosting.constant.JobStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class JobUpdateRequest {

    @NotBlank(message = "Job posting system positioning title cannot be blank")
    @Size(min = 3, max = 100, message = "Title metrics length constraint violation [3-100]")
    private String title;

    @NotBlank(message = "Description context profiling target is mandatory")
    private String description;

    @NotBlank(message = "Geographic destination configuration metadata is required")
    private String location;

    @PositiveOrZero(message = "Minimum compensation cannot accept negative float properties")
    private BigDecimal salaryMin;

    @PositiveOrZero(message = "Ceiling tracking range boundaries must evaluate positive values")
    private BigDecimal salaryMax;

    @NotNull(message = "Job initialization engine status mapping is required")
    private JobStatus status;

    @Valid
    private List<JobRequirementRequest> requirements;

    private List<@NotBlank(message = "Benefit description string cannot be empty") String> benefits;
}