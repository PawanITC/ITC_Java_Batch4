package com.itc.linkedin.connections_service.repository;
import com.itc.linkedin.connections_service.entity.Connection;
import com.itc.linkedin.connections_service.entity.ConnectionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ConnectionRepository extends JpaRepository<Connection, UUID> {


    @Query("""
        SELECT c FROM Connection c
        WHERE 
        (c.requesterId = :userOneId AND c.receiverId = :userTwoId)
        OR
        (c.requesterId = :userTwoId AND c.receiverId = :userOneId)
    """)
    Optional<Connection> findConnectionBetweenUsers(
            UUID userOneId,
            UUID userTwoId
    );

    Page<Connection> findByReceiverIdAndStatus(
            UUID receiverId,
            ConnectionStatus status,
            Pageable pageable
    );

    Page<Connection> findByRequesterIdAndStatus(
            UUID requesterId,
            ConnectionStatus status,
            Pageable pageable
    );

    @Query("""
        SELECT c FROM Connection c
        WHERE 
        (c.requesterId = :userId OR c.receiverId = :userId)
        AND c.status = :status
    """)
    Page<Connection> findUserConnections(
            UUID userId,
            ConnectionStatus status,
            Pageable pageable
    );


}
