package com.itclinkedin.userprofile.repository;

import com.itclinkedin.userprofile.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LanguageRepository extends JpaRepository<Language, UUID> {

    List<Language> findByUserProfile_Id(UUID profileId);
}