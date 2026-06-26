package com.itc.linkedin.connections_service.service;

import com.itc.linkedin.connections_service.dto.ConnectionStatusResponse;

import java.util.Optional;
import java.util.UUID;

public interface ConnectionCacheService {

    Optional<ConnectionStatusResponse> getConnectionStatus(UUID userId, UUID targetUserId);

    void cacheConnectionStatus(UUID userId, UUID targetUserId, ConnectionStatusResponse response);

    void evictConnectionStatus(UUID userId, UUID targetUserId);

    void evictConnectionCount(UUID userId);
}
