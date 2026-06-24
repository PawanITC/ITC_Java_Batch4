package com.itclinkedin.jobPosting.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class JobRequirementResponse {
    private UUID id;
    private String requirement;
    private Boolean isMandatory;
}