package com.itclinkedin.userprofile.unit.service;
import com.itclinkedin.userprofile.dto.request.CreateSkillRequest;
import com.itclinkedin.userprofile.dto.response.SkillResponse;
import com.itclinkedin.userprofile.entity.Skill;
import com.itclinkedin.userprofile.entity.UserProfile;
import com.itclinkedin.userprofile.mapper.SkillMapper;
import com.itclinkedin.userprofile.repository.SkillRepository;
import com.itclinkedin.userprofile.repository.UserProfileRepository;
import com.itclinkedin.userprofile.service.impl.SkillServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillServiceImpl skillService;

    private UUID profileId;
    private UUID skillId;

    private CreateSkillRequest request;
    private UserProfile profile;
    private Skill skill;
    private SkillResponse response;

    @BeforeEach
    void setUp() {

        profileId = UUID.randomUUID();
        skillId = UUID.randomUUID();

        request = new CreateSkillRequest();
        request.setProfileId(profileId);
        request.setSkillName("Java");
        request.setEndorsementCount(5);

        profile = new UserProfile();
        profile.setId(profileId);

        skill = new Skill();
        skill.setId(skillId);
        skill.setSkillName("Java");
        skill.setEndorsementCount(5);

        response = new SkillResponse();
        response.setId(skillId);
        response.setSkillName("Java");
        response.setEndorsementCount(5);
    }

    // ---------------- ADD SUCCESS ----------------

    @Test
    void shouldAddSkillSuccessfully() {

        given(userProfileRepository.findById(profileId))
                .willReturn(Optional.of(profile));

        given(skillMapper.toEntity(request)).willReturn(skill);
        given(skillRepository.save(skill)).willReturn(skill);
        given(skillMapper.toResponse(skill)).willReturn(response);

        SkillResponse result = skillService.addSkill(request);

        assertNotNull(result);
        assertEquals("Java", result.getSkillName());
    }

    // ---------------- ADD FAIL ----------------

    @Test
    void shouldFailWhenProfileNotFound() {

        given(userProfileRepository.findById(profileId))
                .willReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> skillService.addSkill(request));

        assertEquals("Profile not found", ex.getMessage());
    }

    // ---------------- GET BY PROFILE ----------------

    @Test
    void shouldGetSkillsByProfile() {

        given(skillRepository.findByUserProfile_Id(profileId))
                .willReturn(List.of(skill));

        given(skillMapper.toResponse(skill)).willReturn(response);

        List<SkillResponse> result = skillService.getByProfileId(profileId);

        assertEquals(1, result.size());
    }

    // ---------------- UPDATE SUCCESS ----------------

    @Test
    void shouldUpdateSkillSuccessfully() {

        given(skillRepository.findById(skillId))
                .willReturn(Optional.of(skill));

        given(skillRepository.save(skill)).willReturn(skill);
        given(skillMapper.toResponse(skill)).willReturn(response);

        SkillResponse result =
                skillService.updateSkill(skillId, request);

        assertEquals("Java", result.getSkillName());
    }

    // ---------------- UPDATE FAIL ----------------

    @Test
    void shouldFailWhenSkillNotFound() {

        given(skillRepository.findById(skillId))
                .willReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> skillService.updateSkill(skillId, request));

        assertEquals("Skill not found", ex.getMessage());
    }

    // ---------------- DELETE SUCCESS ----------------

    @Test
    void shouldDeleteSkillSuccessfully() {

        given(skillRepository.findById(skillId))
                .willReturn(Optional.of(skill));

        skillService.deleteSkill(skillId);

        then(skillRepository).should().delete(skill);
    }

    // ---------------- DELETE FAIL ----------------

    @Test
    void shouldFailWhenDeleteSkillNotFound() {

        given(skillRepository.findById(skillId))
                .willReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> skillService.deleteSkill(skillId));

        assertEquals("Skill not found", ex.getMessage());
    }
}