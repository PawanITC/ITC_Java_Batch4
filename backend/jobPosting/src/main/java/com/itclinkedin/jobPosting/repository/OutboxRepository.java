package com.itclinkedin.jobPosting.repository;

import com.itclinkedin.jobPosting.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByProcessedFalseOrderByCreatedAtAsc();
}