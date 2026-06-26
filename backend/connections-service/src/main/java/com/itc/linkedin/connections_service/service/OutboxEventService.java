package com.itc.linkedin.connections_service.service;

import com.itc.linkedin.connections_service.entity.Connection;

import java.util.UUID;

public interface OutboxEventService {

    void saveConnectionRequestSentEvent(Connection connection);

    void saveConnectionRequestAcceptedEvent(Connection connection);

    void saveConnectionRequestRejectedEvent(Connection connection);

    void saveConnectionRequestCancelledEvent(Connection connection);

    void saveUserBlockedEvent(UUID currentUserId, UUID targetUserId);

    void saveUserUnblockedEvent(UUID currentUserId, UUID targetUserId);
}
