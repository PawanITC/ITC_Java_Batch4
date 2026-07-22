package com.itc.linkedin.connections_service.repository;
import com.itc.linkedin.connections_service.entity.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BlockedUserRepository extends JpaRepository<BlockedUser, UUID> {

    boolean existsByBlockerIdAndBlockedId(UUID blockerId, UUID blockedId);

    Optional<BlockedUser> findByBlockerIdAndBlockedId(UUID blockerId, UUID blockedId);

    List<BlockedUser> findByBlockerId(UUID blockerId);

    default boolean existsBlockBetweenUsers(UUID userOneId, UUID userTwoId) {
        return existsByBlockerIdAndBlockedId(userOneId, userTwoId)
                || existsByBlockerIdAndBlockedId(userTwoId, userOneId);
    }
}
