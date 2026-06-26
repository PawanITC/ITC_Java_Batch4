package com.itc.linkedin.connections_service.service;

import com.itc.linkedin.connections_service.dto.ConnectionResponse;
import com.itc.linkedin.connections_service.dto.ConnectionStatusResponse;
import com.itc.linkedin.connections_service.dto.PageResponse;
import com.itc.linkedin.connections_service.dto.RelationshipStatus;
import com.itc.linkedin.connections_service.entity.BlockedUser;
import com.itc.linkedin.connections_service.entity.Connection;
import com.itc.linkedin.connections_service.entity.ConnectionStatus;
import com.itc.linkedin.connections_service.exception.ForbiddenException;
import com.itc.linkedin.connections_service.exception.ResourceNotFoundException;
import com.itc.linkedin.connections_service.mapper.ConnectionMapper;
import com.itc.linkedin.connections_service.repository.BlockedUserRepository;
import com.itc.linkedin.connections_service.repository.ConnectionRepository;
import com.itc.linkedin.connections_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private ConnectionRepository connectionRepository;

    private BlockedUserRepository blockedUserRepository;

    private OutboxEventRepository outboxEventRepository;

    private ConnectionMapper connectionMapper;

    private OutboxEventService outboxEventService;

    private ConnectionCacheService connectionCacheService;


    @Override
    @Transactional(readOnly = true)
    public ConnectionStatusResponse checkConnectionStatus(
            UUID currentUserId,
            UUID targetUserId
    ) throws BadRequestException {
        log.info("check_connection_status_started currentUserId={} targetUserId={}",
                currentUserId, targetUserId);

        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException("You cannot check connection status with yourself");
        }

//        Optional<ConnectionStatusResponse> cachedStatus =
//                connectionCacheService.getConnectionStatus(currentUserId, targetUserId);

//        if (cachedStatus.isPresent()) {
//            log.info("check_connection_status_cache_hit currentUserId={} targetUserId={}",
//                    currentUserId, targetUserId);
//            return cachedStatus.get();
//        }

//        log.info("check_connection_status_cache_miss currentUserId={} targetUserId={}",
//                currentUserId, targetUserId);

        ConnectionStatusResponse response = calculateConnectionStatus(
                currentUserId,
                targetUserId
        );

//        connectionCacheService.cacheConnectionStatus(
//                currentUserId,
//                targetUserId,
//                response
//        );

        return response;
    }

    private ConnectionStatusResponse calculateConnectionStatus(
            UUID currentUserId,
            UUID targetUserId
    ) throws BadRequestException {
        if (blockedUserRepository.existsByBlockerIdAndBlockedId(currentUserId, targetUserId)) {
            return new ConnectionStatusResponse(
                    targetUserId,
                    RelationshipStatus.BLOCKED_BY_ME,
                    "You have blocked this user"
            );
        }

        if (blockedUserRepository.existsByBlockerIdAndBlockedId(targetUserId, currentUserId)) {
            return new ConnectionStatusResponse(
                    targetUserId,
                    RelationshipStatus.BLOCKED_BY_OTHER_USER,
                    "This user has blocked you"
            );
        }

        Optional<Connection> existingConnection =
                connectionRepository.findConnectionBetweenUsers(currentUserId, targetUserId);

        if (existingConnection.isEmpty()) {
            return new ConnectionStatusResponse(
                    targetUserId,
                    RelationshipStatus.NONE,
                    "No connection exists"
            );
        }

        Connection connection = existingConnection.get();

        if (connection.getStatus() == ConnectionStatus.ACCEPTED) {
            return new ConnectionStatusResponse(
                    targetUserId,
                    RelationshipStatus.CONNECTED,
                    "User is already connected with you"
            );
        }

        if (connection.getStatus() == ConnectionStatus.PENDING) {
            if (connection.getRequesterId().equals(currentUserId)) {
                return new ConnectionStatusResponse(
                        targetUserId,
                        RelationshipStatus.PENDING_SENT,
                        "Connection request already sent"
                );
            }

            return new ConnectionStatusResponse(
                    targetUserId,
                    RelationshipStatus.PENDING_RECEIVED,
                    "This user has sent you a connection request"
            );
        }

        return new ConnectionStatusResponse(
                targetUserId,
                RelationshipStatus.NONE,
                "No active connection exists"
        );
    }

    @Override
    @Transactional
    public void blockUser(UUID currentUserId, UUID targetUserId) throws BadRequestException {

        log.info("block_user_started blockerId={} blockedId={}",
                currentUserId, targetUserId);

        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException("You cannot block yourself");
        }

        boolean alreadyBlocked = blockedUserRepository.existsByBlockerIdAndBlockedId(
                currentUserId,
                targetUserId
        );

        if (alreadyBlocked) {
            throw new BadRequestException("User is already blocked");
        }

        Optional<Connection> existingConnection =
                connectionRepository.findConnectionBetweenUsers(currentUserId, targetUserId);

        Instant now = Instant.now();

        existingConnection.ifPresent(connection -> {
            connection.setStatus(ConnectionStatus.REMOVED);
            connection.setUpdatedAt(now);
            connectionRepository.save(connection);
        });

        BlockedUser blockedUser = BlockedUser.builder()
                .id(UUID.randomUUID())
                .blockerId(currentUserId)
                .blockedId(targetUserId)
                .createdAt(now)
                .build();

        blockedUserRepository.save(blockedUser);

        outboxEventService.saveUserBlockedEvent(
                currentUserId,
                targetUserId
        );
        //applyCacheEvict(currentUserId, targetUserId);

        log.info("block_user_completed blockerId={} blockedId={}",
                currentUserId, targetUserId);
    }

    @Override
    @Transactional
    public void unblockUser(UUID currentUserId, UUID targetUserId) throws BadRequestException {

        log.info("unblock_user_started blockerId={} blockedId={}",
                currentUserId, targetUserId);

        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException("You cannot unblock yourself");
        }

        BlockedUser blockedUser = blockedUserRepository
                .findByBlockerIdAndBlockedId(currentUserId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Blocked user record not found"));

        blockedUserRepository.delete(blockedUser);

        outboxEventService.saveUserUnblockedEvent(
                currentUserId,
                targetUserId
        );
       // applyCacheEvict(currentUserId, targetUserId);

        log.info("unblock_user_completed blockerId={} blockedId={}",
                currentUserId, targetUserId);
    }

    @Override
    @Transactional()
    public ConnectionResponse sendConnectionRequest(UUID currentUserId, UUID receiverId) throws BadRequestException {
        log.info("send_connection_request_started requesterId={} receiverId={}",
                currentUserId, receiverId);

        if (currentUserId.equals(receiverId)) {
            throw new BadRequestException("You cannot send connection request to yourself");
        }

        boolean blocked = blockedUserRepository.existsBlockBetweenUsers(
                currentUserId,
                receiverId
        );

        if (blocked) {
            throw new BadRequestException("Connection request is not allowed between these users");
        }

        Optional<Connection> existingConnection =
                connectionRepository.findConnectionBetweenUsers(currentUserId, receiverId);

        if (existingConnection.isPresent()) {
            Connection connection = existingConnection.get();

            if (connection.getStatus() == ConnectionStatus.PENDING) {
                throw new BadRequestException("Connection request already exists");
            }

            if (connection.getStatus() == ConnectionStatus.ACCEPTED) {
                throw new BadRequestException("Users are already connected");
            }
        }

        Instant now = Instant.now();

        Connection connection = Connection.builder()
                .id(UUID.randomUUID())
                .requesterId(currentUserId)
                .receiverId(receiverId)
                .status(ConnectionStatus.PENDING)
                .requestedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Connection savedConnection = connectionRepository.save(connection);

        // Later this event will be published to Kafka using Outbox Pattern
        outboxEventService.saveConnectionRequestSentEvent(savedConnection);
        log.info("send_connection_request_completed connectionId={} requesterId={} receiverId={}",
                savedConnection.getId(), currentUserId, receiverId);
        //applyCacheEvict(currentUserId,receiverId);

        return connectionMapper.toResponse(savedConnection);
    }

    @Override
    @Transactional
    public ConnectionResponse acceptConnectionRequest(UUID currentUserId, UUID requestId) throws BadRequestException {
        log.info("accept_connection_request_started requestId={} currentUserId={}",
                requestId, currentUserId);

        Connection connection = connectionRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));

        if (!connection.getReceiverId().equals(currentUserId)) {
            throw new ForbiddenException("Only the receiver can accept this connection request");
        }

        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new BadRequestException("Only pending connection requests can be accepted");
        }

        boolean blocked = blockedUserRepository.existsBlockBetweenUsers(
                connection.getRequesterId(),
                connection.getReceiverId()
        );

        if (blocked) {
            throw new BadRequestException("Cannot accept request because one user has blocked the other");
        }

        Instant now = Instant.now();

        connection.setStatus(ConnectionStatus.ACCEPTED);
        connection.setRespondedAt(now);
        connection.setUpdatedAt(now);

        Connection savedConnection = connectionRepository.save(connection);

        // Later this event will be published to Kafka using Outbox Pattern
        outboxEventService.saveConnectionRequestAcceptedEvent(savedConnection);
       // applyCacheEvict(connection.getRequesterId(),connection.getReceiverId());
        log.info("accept_connection_request_completed connectionId={} acceptedBy={}",
                savedConnection.getId(), currentUserId);

        return connectionMapper.toResponse(savedConnection);
    }

    @Override
    @Transactional
    public ConnectionResponse rejectConnectionRequest(UUID currentUserId, UUID requestId) throws BadRequestException {

        log.info("reject_connection_request_started requestId={} currentUserId={}",
                requestId, currentUserId);

        Connection connection = connectionRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));

        if (!connection.getReceiverId().equals(currentUserId)) {
            throw new ForbiddenException("Only the receiver can reject this connection request");
        }

        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new BadRequestException("Only pending connection requests can be rejected");
        }

        Instant now = Instant.now();

        connection.setStatus(ConnectionStatus.REJECTED);
        connection.setRespondedAt(now);
        connection.setUpdatedAt(now);

        Connection savedConnection = connectionRepository.save(connection);

        outboxEventService.saveConnectionRequestRejectedEvent(savedConnection);
        //applyCacheEvict(connection.getRequesterId(),connection.getReceiverId());

        log.info("reject_connection_request_completed connectionId={} rejectedBy={}",
                savedConnection.getId(), currentUserId);

        return connectionMapper.toResponse(savedConnection);
    }
    @Override
    @Transactional
    public ConnectionResponse cancelConnectionRequest(UUID currentUserId, UUID requestId) throws BadRequestException {

        log.info("cancel_connection_request_started requestId={} currentUserId={}",
                requestId, currentUserId);

        Connection connection = connectionRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection request not found"));

        if (!connection.getRequesterId().equals(currentUserId)) {
            throw new ForbiddenException("Only the requester can cancel this connection request");
        }

        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new BadRequestException("Only pending connection requests can be cancelled");
        }

        Instant now = Instant.now();

        connection.setStatus(ConnectionStatus.CANCELLED);
        connection.setUpdatedAt(now);

        Connection savedConnection = connectionRepository.save(connection);

        outboxEventService.saveConnectionRequestCancelledEvent(savedConnection);
       // applyCacheEvict(connection.getRequesterId(),connection.getReceiverId());

        log.info("cancel_connection_request_completed connectionId={} cancelledBy={}",
                savedConnection.getId(), currentUserId);

        return connectionMapper.toResponse(savedConnection);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ConnectionResponse> getMyConnections(
            UUID currentUserId,
            int page,
            int size
    ) {
        log.info("get_my_connections_started userId={} page={} size={}",
                currentUserId, page, size);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );

        Page<ConnectionResponse> responsePage = connectionRepository
                .findUserConnections(currentUserId, ConnectionStatus.ACCEPTED, pageable)
                .map(connectionMapper::toResponse);

        log.info("get_my_connections_completed userId={} totalElements={}",
                currentUserId, responsePage.getTotalElements());

        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ConnectionResponse> getReceivedRequests(
            UUID currentUserId,
            int page,
            int size
    ) {
        log.info("get_received_requests_started userId={} page={} size={}",
                currentUserId, page, size);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "requestedAt")
        );

        Page<ConnectionResponse> responsePage = connectionRepository
                .findByReceiverIdAndStatus(currentUserId, ConnectionStatus.PENDING, pageable)
                .map(connectionMapper::toResponse);

        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ConnectionResponse> getSentRequests(
            UUID currentUserId,
            int page,
            int size
    ) {
        log.info("get_sent_requests_started userId={} page={} size={}",
                currentUserId, page, size);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "requestedAt")
        );

        Page<ConnectionResponse> responsePage = connectionRepository
                .findByRequesterIdAndStatus(currentUserId, ConnectionStatus.PENDING, pageable)
                .map(connectionMapper::toResponse);

        return PageResponse.from(responsePage);
    }

    @Transactional(readOnly = true)
    public List<UUID> getBlockedUsers(UUID currentUserId) {
        return blockedUserRepository.findByBlockerId(currentUserId)
                .stream()
                .map(BlockedUser::getBlockedId)
                .toList();
    }

    private void applyCacheEvict(UUID currentUserId, UUID targetUserId) {
//        connectionCacheService.evictConnectionStatus(currentUserId, targetUserId);
//        connectionCacheService.evictConnectionCount(currentUserId);
//        connectionCacheService.evictConnectionCount(targetUserId);

    }
}

