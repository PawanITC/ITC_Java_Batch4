package com.itclinkedin.jobPosting.service;

import com.itclinkedin.jobPosting.dto.request.JobPostRequest;
import com.itclinkedin.jobPosting.dto.request.JobUpdateRequest;
import com.itclinkedin.jobPosting.dto.response.JobPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface JobPostService {
    JobPostResponse createJob(JobPostRequest request);
    JobPostResponse getJobById(UUID id, UUID companyId);
    Page<JobPostResponse> getJobsByCompanyId(UUID companyId, Pageable pageable);
    Page<JobPostResponse> getAllJobs(Pageable pageable);
    JobPostResponse updateJob(UUID id, UUID companyId, JobUpdateRequest request);
    void deleteJob(UUID id, UUID companyId);
}