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

        // 1. Find profile from DB
        UserProfile profile = userProfileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new RuntimeException(
                        "Profile not found with id: " + request.getProfileId()
                ));
        System.out.println("Request Profile ID in experience = " + request.getProfileId());
        System.out.println(profile+ " hwjwkjh");

        // 2. Convert DTO → Entity
        Experience experience = experienceMapper.toEntity(request);

        // 3. Set relationship manually
        experience.setUserProfile(profile);

        // 4. Save to DB
        Experience saved = experienceRepository.save(experience);

        // 5. Convert Entity → Response DTO
        return experienceMapper.toResponse(saved);
    }

    @Override
    public List<ExperienceResponse> getByProfileId(UUID profileId) {

        return experienceRepository.findByUserProfile_Id(profileId)
                .stream()
                .map(experienceMapper::toResponse)
                .toList();
    }
}