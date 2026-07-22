package com.itclinkedin.jobPosting.controller;

import com.itclinkedin.jobPosting.dto.request.JobPostRequest;
import com.itclinkedin.jobPosting.dto.request.JobUpdateRequest;
import com.itclinkedin.jobPosting.dto.response.JobPostResponse;
import com.itclinkedin.jobPosting.service.JobPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Posting Controller", description = "High performance APIs across multi-shard layouts")
public class JobPostController {

    private final JobPostService jobPostService;

    @PostMapping
    @Operation(summary = "Publish a new job position across partitions")
    public ResponseEntity<JobPostResponse> createJob(@Valid @RequestBody JobPostRequest request) {
        return new ResponseEntity<>(jobPostService.createJob(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Fetch explicit single record from specific cluster target location")
    public ResponseEntity<JobPostResponse> getJobById(
            @PathVariable UUID id,
            @RequestParam UUID companyId) {
        return ResponseEntity.ok(jobPostService.getJobById(id, companyId));
    }

    @GetMapping
    @Operation(summary = "Fetch all job records with global pagination, optionally filtered by company")
    public ResponseEntity<Page<JobPostResponse>> getJobs(
            @RequestParam(required = false) UUID companyId,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        if (companyId != null) {
            return ResponseEntity.ok(jobPostService.getJobsByCompanyId(companyId, pageable));
        }

        return ResponseEntity.ok(jobPostService.getAllJobs(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modify fields of a specific existing job position")
    public ResponseEntity<JobPostResponse> updateJob(
            @PathVariable UUID id,
            @RequestParam UUID companyId,
            @Valid @RequestBody JobUpdateRequest request) {
        return ResponseEntity.ok(jobPostService.updateJob(id, companyId, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Permanently remove a job post mapping record from its partition context")
    public ResponseEntity<Void> deleteJob(
            @PathVariable UUID id,
            @RequestParam UUID companyId) {
        jobPostService.deleteJob(id, companyId);
        return ResponseEntity.noContent().build(); // Standard 204 response for successful deletion
    }
}