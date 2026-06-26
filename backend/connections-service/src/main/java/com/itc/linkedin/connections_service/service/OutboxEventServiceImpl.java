package com.itc.linkedin.connections_service.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.itc.linkedin.connections_service.dto.ConnectionEventPayload;
import com.itc.linkedin.connections_service.dto.UserBlockEventPayload;
import com.itc.linkedin.connections_service.entity.Connection;
import com.itc.linkedin.connections_service.entity.OutboxEvent;
import com.itc.linkedin.connections_service.entity.OutboxEventStatus;
import com.itc.linkedin.connections_service.event.ConnectionEventType;
import com.itc.linkedin.connections_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxEventServiceImpl implements OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper;

    @Override
    public void saveConnectionRequestSentEvent(Connection connection) {
        saveConnectionEvent(connection, ConnectionEventType.CONNECTION_REQUEST_SENT);
    }

    @Override
    public void saveConnectionRequestAcceptedEvent(Connection connection) {
        saveConnectionEvent(connection, ConnectionEventType.CONNECTION_REQUEST_ACCEPTED);
    }

    @Override
    public void saveConnectionRequestRejectedEvent(Connection connection) {
        saveConnectionEvent(connection, ConnectionEventType.CONNECTION_REQUEST_REJECTED);
    }

    @Override
    public void saveConnectionRequestCancelledEvent(Connection connection) {
        saveConnectionEvent(connection, ConnectionEventType.CONNECTION_REQUEST_CANCELLED);
    }

    @Override
    public void saveUserBlockedEvent(UUID blockerId, UUID blockedId) {
        saveUserBlockEvent(blockerId, blockedId, ConnectionEventType.USER_BLOCKED);
    }

    @Override
    public void saveUserUnblockedEvent(UUID blockerId, UUID blockedId) {
        saveUserBlockEvent(blockerId, blockedId, ConnectionEventType.USER_UNBLOCKED);
    }

    private void saveConnectionEvent(
            Connection connection,
            ConnectionEventType eventType
    ) {
        UUID eventId = UUID.randomUUID();

        ConnectionEventPayload payload = new ConnectionEventPayload(
                eventId,
                eventType.name(),
                connection.getId(),
                connection.getRequesterId(),
                connection.getReceiverId(),
                Instant.now()
        );

        saveOutboxEvent(
                eventId,
                connection.getId(),
                "Connection",
                eventType.name(),
                payload
        );
    }

    private void saveUserBlockEvent(
            UUID blockerId,
            UUID blockedId,
            ConnectionEventType eventType
    ) {
        UUID eventId = UUID.randomUUID();

        UserBlockEventPayload payload = new UserBlockEventPayload(
                eventId,
                eventType.name(),
                blockerId,
                blockedId,
                Instant.now()
        );

        saveOutboxEvent(
                eventId,
                blockerId,
                "BlockedUser",
                eventType.name(),
                payload
        );
    }

    private void saveOutboxEvent(
            UUID eventId,
            UUID aggregateId,
            String aggregateType,
            String eventType,
            Object payload
    ) {
        try {
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .id(eventId)
                    .aggregateId(aggregateId)
                    .aggregateType(aggregateType)
                    .eventType(eventType)
                    .payload(objectMapper.writeValueAsString(payload))
                    .status(OutboxEventStatus.PENDING)
                    .createdAt(Instant.now())
                    .build();

            outboxEventRepository.save(outboxEvent);

        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize outbox event", ex);
        }
    }
}