package com.itclinkedin.userprofile.unit.service;
import com.itclinkedin.userprofile.dto.request.CreateEducationRequest;
import com.itclinkedin.userprofile.dto.response.EducationResponse;
import com.itclinkedin.userprofile.entity.Education;
import com.itclinkedin.userprofile.entity.UserProfile;
import com.itclinkedin.userprofile.mapper.EducationMapper;
import com.itclinkedin.userprofile.repository.EducationRepository;
import com.itclinkedin.userprofile.repository.UserProfileRepository;
import com.itclinkedin.userprofile.service.impl.EducationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {
    @Mock
    private EducationRepository educationRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private EducationMapper educationMapper;

    @InjectMocks
    private EducationServiceImpl educationService;

    private UUID profileId;
    private UUID educationId;

    private CreateEducationRequest request;
    private UserProfile profile;
    private Education education;
    private EducationResponse response;

    @BeforeEach
    void setUp() {

        profileId = UUID.randomUUID();
        educationId = UUID.randomUUID();

        request = new CreateEducationRequest();
        request.setProfileId(profileId);
        request.setSchoolName("MIT");
        request.setDegree("Masters");
        request.setFieldOfStudy("Computer Science");
        request.setStartYear(2020);
        request.setEndYear(2022);

        profile = new UserProfile();
        profile.setId(profileId);

        education = new Education();
        education.setId(educationId);
        education.setSchoolName("MIT");
        education.setDegree("Masters");
        education.setFieldOfStudy("Computer Science");

        response = new EducationResponse();
        response.setId(educationId);
        response.setSchoolName("MIT");
        response.setDegree("Masters");
        response.setFieldOfStudy("Computer Science");
    }

    @Test
    void shouldAddEducationSuccessfully() {

        given(userProfileRepository.findById(profileId))
                .willReturn(Optional.of(profile));

        given(educationMapper.toEntity(request))
                .willReturn(education);

        given(educationRepository.save(education))
                .willReturn(education);

        given(educationMapper.toResponse(education))
                .willReturn(response);

        EducationResponse result =
                educationService.addEducation(request);

        assertThat(result).isNotNull();
        assertThat(result.getSchoolName()).isEqualTo("MIT");

        then(userProfileRepository)
                .should()
                .findById(profileId);

        then(educationRepository)
                .should()
                .save(education);
    }

    @Test
    void shouldThrowExceptionWhenProfileNotFoundWhileAddingEducation() {

        given(userProfileRepository.findById(profileId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                educationService.addEducation(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Profile not found");

        then(educationRepository)
                .shouldHaveNoInteractions();
    }

    @Test
    void shouldReturnEducationListByProfileId() {

        given(educationRepository.findByUserProfile_Id(profileId))
                .willReturn(List.of(education));

        given(educationMapper.toResponse(education))
                .willReturn(response);

        List<EducationResponse> result =
                educationService.getByProfileId(profileId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSchoolName())
                .isEqualTo("MIT");
    }

    @Test
    void shouldReturnEmptyEducationList() {

        given(educationRepository.findByUserProfile_Id(profileId))
                .willReturn(Collections.emptyList());

        List<EducationResponse> result =
                educationService.getByProfileId(profileId);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateEducationSuccessfully() {

        given(educationRepository.findById(educationId))
                .willReturn(Optional.of(education));

        given(educationRepository.save(any(Education.class)))
                .willReturn(education);

        given(educationMapper.toResponse(education))
                .willReturn(response);

        EducationResponse result =
                educationService.updateEducation(
                        educationId,
                        request
                );

        assertThat(result).isNotNull();
        assertThat(result.getSchoolName())
                .isEqualTo("MIT");

        then(educationRepository)
                .should()
                .save(any(Education.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingEducation() {

        given(educationRepository.findById(educationId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                educationService.updateEducation(
                        educationId,
                        request
                ))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Education not found");

        then(educationRepository)
                .should(never())
                .save(any());
    }

    @Test
    void shouldDeleteEducationSuccessfully() {

        given(educationRepository.findById(educationId))
                .willReturn(Optional.of(education));

        educationService.deleteEducation(educationId);

        then(educationRepository)
                .should()
                .delete(education);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingEducation() {

        given(educationRepository.findById(educationId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() ->
                educationService.deleteEducation(educationId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Education not found");

        then(educationRepository)
                .should(never())
                .delete(any());
    }

}
