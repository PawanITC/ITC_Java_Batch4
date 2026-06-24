package com.itclinkedin.jobPosting.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itclinkedin.jobPosting.constant.JobStatus;
import com.itclinkedin.jobPosting.dto.request.JobPostRequest;
import com.itclinkedin.jobPosting.dto.request.JobUpdateRequest;
import com.itclinkedin.jobPosting.dto.response.JobPostResponse;
import com.itclinkedin.jobPosting.entity.JobPost;
import com.itclinkedin.jobPosting.entity.OutboxEvent;
import com.itclinkedin.jobPosting.exception.ResourceNotFoundException;
import com.itclinkedin.jobPosting.mapper.JobPostMapper;
import com.itclinkedin.jobPosting.repository.JobPostRepository;
import com.itclinkedin.jobPosting.repository.OutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobPostServiceImplTest {

    @Mock private JobPostRepository jobPostRepository;
    @Mock private JobPostMapper jobPostMapper;
    @Mock private OutboxRepository outboxRepository;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks private JobPostServiceImpl jobPostService;

    private UUID jobId;
    private UUID companyId;
    private JobPost jobPost;
    private JobPostRequest jobPostRequest;
    private JobUpdateRequest jobUpdateRequest;
    private JobPostResponse jobPostResponse;

    @BeforeEach
    void setUp() {
        jobId = UUID.randomUUID();
        companyId = UUID.randomUUID();

        jobPost = new JobPost();
        jobPost.setId(jobId);
        jobPost.setCompanyId(companyId);
        jobPost.setTitle("Staff Architect");
        jobPost.setLocation("New York");
        jobPost.setStatus(JobStatus.OPEN); // Fixed: Changed from mockEntity to jobPost

        jobPostRequest = new JobPostRequest();
        jobUpdateRequest = new JobUpdateRequest();
        jobPostResponse = new JobPostResponse();
    }

    // --- CREATE JOB TESTS ---

    @Test
    void createJob_Success() throws Exception {
        when(jobPostMapper.toEntity(jobPostRequest)).thenReturn(jobPost);
        when(jobPostRepository.save(jobPost)).thenReturn(jobPost);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"mock\":\"payload\"}");
        when(jobPostMapper.toResponse(jobPost)).thenReturn(jobPostResponse);

        JobPostResponse result = jobPostService.createJob(jobPostRequest);

        assertNotNull(result);
        verify(jobPostRepository, times(1)).save(jobPost);
        verify(outboxRepository, times(1)).save(any(OutboxEvent.class));
    }

    @Test
    void createJob_Failure_SerializationException() throws Exception {
        when(jobPostMapper.toEntity(jobPostRequest)).thenReturn(jobPost);
        when(jobPostRepository.save(jobPost)).thenReturn(jobPost);
        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                jobPostService.createJob(jobPostRequest)
        );

        assertTrue(exception.getMessage().contains("Failed to serialize"));
        verify(outboxRepository, never()).save(any());
    }

    // --- GET BY ID TESTS ---

    @Test
    void getJobById_Success() {
        when(jobPostRepository.findByIdAndCompanyId(jobId, companyId)).thenReturn(Optional.of(jobPost));
        when(jobPostMapper.toResponse(jobPost)).thenReturn(jobPostResponse);

        JobPostResponse result = jobPostService.getJobById(jobId, companyId);

        assertNotNull(result);
        verify(jobPostRepository, times(1)).findByIdAndCompanyId(jobId, companyId);
    }

    @Test
    void getJobById_Failure_NotFound() {
        when(jobPostRepository.findByIdAndCompanyId(jobId, companyId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                jobPostService.getJobById(jobId, companyId)
        );
    }

    // --- PAGINATION TESTS ---

    @Test
    void getJobsByCompanyId_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobPost> page = new PageImpl<>(Collections.singletonList(jobPost));

        when(jobPostRepository.findByCompanyId(companyId, pageable)).thenReturn(page);
        when(jobPostMapper.toResponse(any(JobPost.class))).thenReturn(jobPostResponse);

        Page<JobPostResponse> result = jobPostService.getJobsByCompanyId(companyId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(jobPostRepository, times(1)).findByCompanyId(companyId, pageable);
    }

    @Test
    void getAllJobs_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobPost> page = new PageImpl<>(Collections.singletonList(jobPost));

        when(jobPostRepository.findAll(pageable)).thenReturn(page);
        when(jobPostMapper.toResponse(any(JobPost.class))).thenReturn(jobPostResponse);

        Page<JobPostResponse> result = jobPostService.getAllJobs(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(jobPostRepository, times(1)).findAll(pageable);
    }

    // --- UPDATE TESTS ---

    @Test
    void updateJob_Success() {
        when(jobPostRepository.findByIdAndCompanyId(jobId, companyId)).thenReturn(Optional.of(jobPost));
        when(jobPostRepository.save(jobPost)).thenReturn(jobPost);
        when(jobPostMapper.toResponse(jobPost)).thenReturn(jobPostResponse);

        JobPostResponse result = jobPostService.updateJob(jobId, companyId, jobUpdateRequest);

        assertNotNull(result);
        verify(jobPostMapper, times(1)).updateEntityFromDto(jobUpdateRequest, jobPost);
        verify(jobPostRepository, times(1)).save(jobPost);
    }

    @Test
    void updateJob_Failure_NotFound() {
        when(jobPostRepository.findByIdAndCompanyId(jobId, companyId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                jobPostService.updateJob(jobId, companyId, jobUpdateRequest)
        );
        verify(jobPostRepository, never()).save(any());
    }

    // --- DELETE TESTS ---

    @Test
    void deleteJob_Success() {
        when(jobPostRepository.findByIdAndCompanyId(jobId, companyId)).thenReturn(Optional.of(jobPost));

        assertDoesNotThrow(() -> jobPostService.deleteJob(jobId, companyId));
        verify(jobPostRepository, times(1)).delete(jobPost);
    }

    @Test
    void deleteJob_Failure_NotFound() {
        when(jobPostRepository.findByIdAndCompanyId(jobId, companyId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                jobPostService.deleteJob(jobId, companyId)
        );
        verify(jobPostRepository, never()).delete(any());
    }
}