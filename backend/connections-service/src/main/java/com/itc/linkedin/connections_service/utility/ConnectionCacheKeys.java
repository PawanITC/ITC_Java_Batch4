package com.itc.linkedin.connections_service.utility;
import java.util.UUID;

public final class ConnectionCacheKeys {

    private ConnectionCacheKeys() {
    }

    public static String connectionStatus(UUID userId, UUID targetUserId) {
        return "connection:status:" + userId + ":" + targetUserId;
    }

    public static String connectionCount(UUID userId) {
        return "connection:count:" + userId;
    }

    public static String blockStatus(UUID userId, UUID targetUserId) {
        return "connection:block:" + userId + ":" + targetUserId;
    }
}