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

        UserProfile profile = userProfileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new RuntimeException(
                        "Profile not found with id: " + request.getProfileId()
                ));

        Education education = educationMapper.toEntity(request);

        education.setUserProfile(profile);

        Education saved = educationRepository.save(education);

        return educationMapper.toResponse(saved);
    }

    @Override
    public List<EducationResponse> getByProfileId(UUID profileId) {

        return educationRepository.findByUserProfile_Id(profileId)
                .stream()
                .map(educationMapper::toResponse)
                .toList();
    }

    @Override
    public EducationResponse updateEducation(UUID educationId,
                                             CreateEducationRequest request) {

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() ->
                        new RuntimeException("Education not found"));

        education.setSchoolName(request.getSchoolName());
        education.setDegree(request.getDegree());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartYear(request.getStartYear());
        education.setEndYear(request.getEndYear());

        Education updated = educationRepository.save(education);

        return educationMapper.toResponse(updated);
    }

    @Override
    public void deleteEducation(UUID educationId) {

        Education education = educationRepository.findById(educationId)
                .orElseThrow(() ->
                        new RuntimeException("Education not found"));

        educationRepository.delete(education);
    }
}