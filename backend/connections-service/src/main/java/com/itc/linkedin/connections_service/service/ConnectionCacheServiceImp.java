package com.itc.linkedin.connections_service.service;
import com.itc.linkedin.connections_service.dto.ConnectionStatusResponse;
import com.itc.linkedin.connections_service.utility.ConnectionCacheKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConnectionCacheServiceImp implements ConnectionCacheService {

    private static final Duration STATUS_TTL = Duration.ofMinutes(10);

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Optional<ConnectionStatusResponse> getConnectionStatus(UUID userId, UUID targetUserId) {
        String key = ConnectionCacheKeys.connectionStatus(userId, targetUserId);

        Object value = redisTemplate.opsForValue().get(key);

        if (value instanceof ConnectionStatusResponse response) {
            return Optional.of(response);
        }

        return Optional.empty();
    }

    @Override
    public void cacheConnectionStatus(
            UUID userId,
            UUID targetUserId,
            ConnectionStatusResponse response
    ) {
        String key = ConnectionCacheKeys.connectionStatus(userId, targetUserId);
        redisTemplate.opsForValue().set(key, response, STATUS_TTL);
    }

    @Override
    public void evictConnectionStatus(UUID userId, UUID targetUserId) {
        redisTemplate.delete(ConnectionCacheKeys.connectionStatus(userId, targetUserId));
        redisTemplate.delete(ConnectionCacheKeys.connectionStatus(targetUserId, userId));
    }

    @Override
    public void evictConnectionCount(UUID userId) {
        redisTemplate.delete(ConnectionCacheKeys.connectionCount(userId));
    }
}
