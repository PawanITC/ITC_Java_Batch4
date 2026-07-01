
package com.itclinkedin.userprofile.repository;

import com.itclinkedin.userprofile.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    boolean existsByEmail(String email);

    Optional<UserProfile> findByEmailIgnoreCase(String email);

    Optional<UserProfile> findByKeycloakUserId(String keycloakUserId);
}
