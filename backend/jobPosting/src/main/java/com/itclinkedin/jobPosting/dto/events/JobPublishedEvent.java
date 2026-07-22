package com.itclinkedin.jobPosting.dto.events;

import com.itclinkedin.jobPosting.constant.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor  // Generates default constructor for serialization engines
@AllArgsConstructor // Generates the constructor you are calling with arguments
public class JobPublishedEvent {
    private UUID companyId;
    private String title;
    private String location;
    private JobStatus status;
}