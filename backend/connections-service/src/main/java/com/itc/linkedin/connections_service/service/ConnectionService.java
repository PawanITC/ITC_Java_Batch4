package com.itc.linkedin.connections_service.service;

import com.itc.linkedin.connections_service.dto.ConnectionResponse;
import com.itc.linkedin.connections_service.dto.ConnectionStatusResponse;
import com.itc.linkedin.connections_service.dto.PageResponse;
import org.apache.coyote.BadRequestException;

import java.util.UUID;

public interface ConnectionService {
    ConnectionStatusResponse checkConnectionStatus(
            UUID currentUserId,
            UUID targetUserId
    ) throws BadRequestException;

    void blockUser(UUID currentUserId, UUID targetUserId) throws BadRequestException;

    void unblockUser(UUID currentUserId, UUID targetUserId) throws BadRequestException;
    ConnectionResponse sendConnectionRequest(UUID currentUserId, UUID receiverId) throws BadRequestException;

    ConnectionResponse acceptConnectionRequest(UUID currentUserId, UUID requestId) throws BadRequestException;

    ConnectionResponse rejectConnectionRequest(UUID currentUserId, UUID requestId) throws BadRequestException;

    ConnectionResponse cancelConnectionRequest(UUID currentUserId, UUID requestId) throws BadRequestException;

    PageResponse<ConnectionResponse> getMyConnections(
            UUID currentUserId,
            int page,
            int size
    );

    PageResponse<ConnectionResponse> getReceivedRequests(
            UUID currentUserId,
            int page,
            int size
    );

    PageResponse<ConnectionResponse> getSentRequests(
            UUID currentUserId,
            int page,
            int size
    );
}
