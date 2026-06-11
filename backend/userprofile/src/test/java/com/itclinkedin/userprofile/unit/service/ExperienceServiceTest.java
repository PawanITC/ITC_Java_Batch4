package com.itclinkedin.userprofile.unit.service;
import com.itclinkedin.userprofile.dto.request.CreateExperienceRequest;
import com.itclinkedin.userprofile.dto.response.ExperienceResponse;
import com.itclinkedin.userprofile.entity.Experience;
import com.itclinkedin.userprofile.entity.UserProfile;
import com.itclinkedin.userprofile.mapper.ExperienceMapper;
import com.itclinkedin.userprofile.repository.ExperienceRepository;
import com.itclinkedin.userprofile.repository.UserProfileRepository;
import com.itclinkedin.userprofile.service.impl.ExperienceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ExperienceMapper experienceMapper;

    @InjectMocks
    private ExperienceServiceImpl experienceService;


    @Test
    void givenValidRequest_whenAddExperience_thenReturnResponse() {

        UUID profileId = UUID.randomUUID();

        CreateExperienceRequest request = new CreateExperienceRequest();
        request.setProfileId(profileId);
        request.setCompanyName("Google");

        UserProfile profile = new UserProfile();
        Experience entity = new Experience();

        ExperienceResponse response = new ExperienceResponse();
        response.setCompanyName("Google");

        when(userProfileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(experienceMapper.toEntity(request)).thenReturn(entity);
        when(experienceRepository.save(entity)).thenReturn(entity);
        when(experienceMapper.toResponse(entity)).thenReturn(response);

        ExperienceResponse result = experienceService.addExperience(request);

        assertNotNull(result);
        assertEquals("Google", result.getCompanyName());

        verify(experienceRepository, times(1)).save(entity);
    }

    @Test
    void givenInvalidProfile_whenAddExperience_thenThrowException() {

        UUID profileId = UUID.randomUUID();

        CreateExperienceRequest request = new CreateExperienceRequest();
        request.setProfileId(profileId);

        when(userProfileRepository.findById(profileId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> experienceService.addExperience(request)
        );

        assertEquals(
                "Profile not found with id: " + profileId,
                exception.getMessage()
        );
    }

    @Test
    void givenProfileId_whenGetExperiences_thenReturnList() {

        UUID profileId = UUID.randomUUID();

        Experience experience = new Experience();
        ExperienceResponse response = new ExperienceResponse();
        response.setCompanyName("Google");

        when(experienceRepository.findByUserProfile_Id(profileId))
                .thenReturn(List.of(experience));

        when(experienceMapper.toResponse(experience)).thenReturn(response);

        List<ExperienceResponse> result = experienceService.getByProfileId(profileId);

        assertEquals(1, result.size());
        assertEquals("Google", result.get(0).getCompanyName());
    }

    @Test
    void givenValidRequest_whenUpdateExperience_thenReturnUpdated() {

        UUID experienceId = UUID.randomUUID();

        CreateExperienceRequest request = new CreateExperienceRequest();
        request.setCompanyName("Amazon");

        Experience experience = new Experience();
        Experience updated = new Experience();

        ExperienceResponse response = new ExperienceResponse();
        response.setCompanyName("Amazon");

        when(experienceRepository.findById(experienceId)).thenReturn(Optional.of(experience));
        when(experienceRepository.save(experience)).thenReturn(updated);
        when(experienceMapper.toResponse(updated)).thenReturn(response);

        ExperienceResponse result = experienceService.updateExperience(experienceId, request);

        assertEquals("Amazon", result.getCompanyName());
        verify(experienceRepository).save(experience);
    }

    @Test
    void givenInvalidExperienceId_whenUpdate_thenThrowException() {

        UUID experienceId = UUID.randomUUID();
        CreateExperienceRequest request = new CreateExperienceRequest();

        when(experienceRepository.findById(experienceId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> experienceService.updateExperience(experienceId, request)
        );

        assertEquals("Experience not found", exception.getMessage());
    }

    // ---------------- DELETE EXPERIENCE ----------------

    @Test
    void givenValidId_whenDelete_thenRepositoryCalled() {

        UUID id = UUID.randomUUID();

        Experience experience = new Experience();

        when(experienceRepository.findById(id)).thenReturn(Optional.of(experience));
        doNothing().when(experienceRepository).delete(experience);

        experienceService.deleteExperience(id);

        verify(experienceRepository).delete(experience);
    }

    @Test
    void givenInvalidId_whenDelete_thenThrowException() {

        UUID id = UUID.randomUUID();

        when(experienceRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> experienceService.deleteExperience(id)
        );

        assertEquals("Experience not found", exception.getMessage());
    }
}