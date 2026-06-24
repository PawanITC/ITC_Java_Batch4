package com.itclinkedin.jobPosting.repository;

import com.itclinkedin.jobPosting.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, UUID> {
    // Shard routing relies heavily on target indexes targeting the composite key pattern:
    Optional<JobPost> findByIdAndCompanyId(UUID id, UUID companyId);
    Page<JobPost> findByCompanyId(UUID companyId, Pageable pageable);
}