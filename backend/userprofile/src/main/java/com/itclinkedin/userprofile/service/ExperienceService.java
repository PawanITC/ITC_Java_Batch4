package com.itclinkedin.userprofile.service;

import com.itclinkedin.userprofile.dto.request.CreateExperienceRequest;
import com.itclinkedin.userprofile.dto.response.ExperienceResponse;

import java.util.List;
import java.util.UUID;

public interface ExperienceService {

    ExperienceResponse addExperience(CreateExperienceRequest request);

    List<ExperienceResponse> getByProfileId(UUID profileId);

    ExperienceResponse updateExperience(
            UUID experienceId,
            CreateExperienceRequest request
    );

    void deleteExperience(UUID experienceId);
}