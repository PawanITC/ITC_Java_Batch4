package com.itclinkedin.jobPosting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobRequirementRequest {
    @NotBlank(message = "Requirement context parameter cannot be blank")
    private String requirement;

    @NotNull(message = "Specify if parameter validation condition is mandatory")
    private Boolean isMandatory;
}