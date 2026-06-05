package com.itclinkedin.userprofile.service.impl;

import com.itclinkedin.userprofile.dto.request.CreateEducationRequest;
import com.itclinkedin.userprofile.dto.response.EducationResponse;
import com.itclinkedin.userprofile.entity.Education;
import com.itclinkedin.userprofile.entity.UserProfile;
import com.itclinkedin.userprofile.mapper.EducationMapper;
import com.itclinkedin.userprofile.repository.EducationRepository;
import com.itclinkedin.userprofile.repository.UserProfileRepository;
import com.itclinkedin.userprofile.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final UserProfileRepository userProfileRepository;
    private final EducationMapper educationMapper;
    @Override
    public EducationResponse addEducation(CreateEducationRequest request) {

        // 1. Find user profile
        UserProfile profile = userProfileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new RuntimeException(
                        "Profile not found with id: " + request.getProfileId()
                ));
        System.out.println("Request Profile ID in EducationServiceImpl = " + request.getProfileId());
System.out.println(profile+"hwjwkjh");
        // 2. Map DTO → Entity
        Education education = educationMapper.toEntity(request);

        // 3. Set relationship manually
        education.setUserProfile(profile);

        // 4. Save to DB
        Education saved = educationRepository.save(education);

        // 5. Convert Entity → Response DTO
        return educationMapper.toResponse(saved);
    }

    @Override
    public List<EducationResponse> getByProfileId(UUID profileId) {

        return educationRepository.findByUserProfile_Id(profileId)
                .stream()
                .map(educationMapper::toResponse)
                .toList();
    }
}