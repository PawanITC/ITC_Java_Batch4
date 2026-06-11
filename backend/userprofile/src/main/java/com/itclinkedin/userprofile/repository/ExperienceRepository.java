
package com.itclinkedin.userprofile.repository;

import com.itclinkedin.userprofile.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExperienceRepository extends JpaRepository<Experience, UUID> {

    List<Experience> findByUserProfile_Id(UUID profileId);
}