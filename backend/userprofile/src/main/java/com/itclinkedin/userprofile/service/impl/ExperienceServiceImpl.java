package com.itclinkedin.userprofile.service.impl;

import com.itclinkedin.userprofile.dto.request.CreateExperienceRequest;
import com.itclinkedin.userprofile.dto.response.ExperienceResponse;
import com.itclinkedin.userprofile.entity.Experience;
import com.itclinkedin.userprofile.entity.UserProfile;
import com.itclinkedin.userprofile.mapper.ExperienceMapper;
import com.itclinkedin.userprofile.repository.ExperienceRepository;
import com.itclinkedin.userprofile.repository.UserProfileRepository;
import com.itclinkedin.userprofile.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserProfileRepository userProfileRepository;
    private final ExperienceMapper experienceMapper;

    @Override
    public ExperienceResponse addExperience(CreateExperienceRequest request) {

        UserProfile profile = userProfileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new RuntimeException(
                        "Profile not found with id: " + request.getProfileId()
                ));

        Experience experience = experienceMapper.toEntity(request);

        experience.setUserProfile(profile);

        Experience saved = experienceRepository.save(experience);

        return experienceMapper.toResponse(saved);
    }

    @Override
    public List<ExperienceResponse> getByProfileId(UUID profileId) {

        return experienceRepository.findByUserProfile_Id(profileId)
                .stream()
                .map(experienceMapper::toResponse)
                .toList();
    }

    @Override
    public ExperienceResponse updateExperience(
            UUID experienceId,
            CreateExperienceRequest request
    ) {

        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() ->
                        new RuntimeException("Experience not found"));

        experience.setCompanyName(request.getCompanyName());
        experience.setTitle(request.getTitle());
        experience.setDescription(request.getDescription());

        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.getEndDate());

        experience.setCurrent(request.getCurrent());

        Experience updated = experienceRepository.save(experience);

        return experienceMapper.toResponse(updated);
    }

    @Override
    public void deleteExperience(UUID experienceId) {

        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() ->
                        new RuntimeException("Experience not found"));

        experienceRepository.delete(experience);
    }
}