package com.itclinkedin.jobPosting.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itclinkedin.jobPosting.dto.events.JobPublishedEvent;
import com.itclinkedin.jobPosting.dto.request.JobPostRequest;
import com.itclinkedin.jobPosting.dto.request.JobUpdateRequest;
import com.itclinkedin.jobPosting.dto.response.JobPostResponse;
import com.itclinkedin.jobPosting.entity.JobPost;
import com.itclinkedin.jobPosting.entity.OutboxEvent;
import com.itclinkedin.jobPosting.exception.ResourceNotFoundException;
import com.itclinkedin.jobPosting.mapper.JobPostMapper;
import com.itclinkedin.jobPosting.repository.JobPostRepository;
import com.itclinkedin.jobPosting.repository.OutboxRepository;
import com.itclinkedin.jobPosting.service.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobPostServiceImpl implements JobPostService {

    private final JobPostRepository jobPostRepository;
    private final JobPostMapper jobPostMapper;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    @Override
    @Transactional
    public JobPostResponse createJob(JobPostRequest request) {
        JobPost jobPost = jobPostMapper.toEntity(request);
        JobPost savedJob = jobPostRepository.save(jobPost);

        // Construct event payload object
        JobPublishedEvent event = new JobPublishedEvent(
                savedJob.getCompanyId(),
                savedJob.getTitle(),
                savedJob.getLocation(),
                savedJob.getStatus()
        );

        try {
            String jsonPayload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType("JobPost")
                    .aggregateId(savedJob.getId().toString())
                    .eventType("JOB_PUBLISHED")
                    .payload(jsonPayload)
                    .processed(false)
                    .build();

            outboxRepository.save(outboxEvent); // Written atomically within the transaction block
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize transaction outbox payload context data", e);
        }

        return jobPostMapper.toResponse(savedJob);
    }

    @Override
    @Transactional(readOnly = true)
    public JobPostResponse getJobById(UUID id, UUID companyId) {
        JobPost job = jobPostRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Job targeting reference pair not found on active shards"));
        return jobPostMapper.toResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobPostResponse> getJobsByCompanyId(UUID companyId, Pageable pageable) {
        Page<JobPost> jobsPage = jobPostRepository.findByCompanyId(companyId, pageable);

        // Map the internal entity Page contents cleanly into your response DTO structure
        return jobsPage.map(jobPostMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobPostResponse> getAllJobs(Pageable pageable) {
        Page<JobPost> jobsPage = jobPostRepository.findAll(pageable); // Uses standard built-in JpaRepository method
        return jobsPage.map(jobPostMapper::toResponse);
    }

    @Override
    @Transactional
    public JobPostResponse updateJob(UUID id, UUID companyId, JobUpdateRequest request) {
        JobPost existingJob = jobPostRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Job matching reference parameters could not be resolved for editing"));

        // Use the newly defined mapstruct method to apply changes onto the entity instance
        jobPostMapper.updateEntityFromDto(request, existingJob);

        JobPost updatedJob = jobPostRepository.save(existingJob);
        return jobPostMapper.toResponse(updatedJob);
    }

    // New Delete Implementation
    @Override
    @Transactional
    public void deleteJob(UUID id, UUID companyId) {
        JobPost existingJob = jobPostRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Job matching reference parameters could not be resolved for deletion"));

        jobPostRepository.delete(existingJob);
    }
}