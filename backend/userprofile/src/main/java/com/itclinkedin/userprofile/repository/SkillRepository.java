package com.itclinkedin.userprofile.repository;

import com.itclinkedin.userprofile.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {

    List<Skill> findByUserProfile_Id(UUID profileId);
}