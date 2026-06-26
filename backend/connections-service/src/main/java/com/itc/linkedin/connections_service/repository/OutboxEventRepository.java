package com.itc.linkedin.connections_service.repository;
import com.itc.linkedin.connections_service.entity.OutboxEvent;
import com.itc.linkedin.connections_service.entity.OutboxEventStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(
            OutboxEventStatus status,
            Pageable pageable
    );
}
