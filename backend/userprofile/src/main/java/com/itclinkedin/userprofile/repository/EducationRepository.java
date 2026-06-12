
package com.itclinkedin.userprofile.repository;

import com.itclinkedin.userprofile.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EducationRepository extends JpaRepository<Education, UUID> {
    List<Education> findByUserProfile_Id(UUID profileId);

}