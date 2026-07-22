package com.itclinkedin.userprofile.unit.service;
import com.itclinkedin.userprofile.dto.request.CreateProfileRequest;
import com.itclinkedin.userprofile.dto.request.UpdateProfileRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;
import com.itclinkedin.userprofile.entity.UserProfile;
import com.itclinkedin.userprofile.exception.ResourceNotFoundException;
import com.itclinkedin.userprofile.mapper.ProfileMapper;
import com.itclinkedin.userprofile.repository.UserProfileRepository;
import com.itclinkedin.userprofile.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.kafka.core.KafkaTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserProfileRepository repository;

    @Mock
    private ProfileMapper mapper;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private ProfileServiceImpl service;

    @Test
    void givenValidRequest_whenCreateProfile_thenReturnSavedProfile() {

        CreateProfileRequest request = new CreateProfileRequest();
        request.setKeycloakUserId(UUID.randomUUID().toString());
        request.setFirstName("Hasnain");
        request.setEmail("hasnain@test.com");

        UserProfile entity = new UserProfile();
        entity.setId(UUID.randomUUID());
        entity.setKeycloakUserId(request.getKeycloakUserId());
        entity.setEmail("hasnain@test.com");

        ProfileResponse response = ProfileResponse.builder()
                .id(entity.getId())
                .email("hasnain@test.com")
                .build();

        when(repository.findByKeycloakUserId(request.getKeycloakUserId())).thenReturn(Optional.empty());
        when(repository.findByEmailIgnoreCase("hasnain@test.com")).thenReturn(Optional.empty());
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        ProfileResponse result = service.create(request);

        assertNotNull(result);
        assertEquals("hasnain@test.com", result.getEmail());

        verify(repository, times(1)).save(entity);
    }

    @Test
    void givenExistingProfileForKeycloakUser_whenCreateProfile_thenReturnExistingProfile() {

        CreateProfileRequest request = new CreateProfileRequest();
        request.setKeycloakUserId(UUID.randomUUID().toString());
        request.setFirstName("Demo");
        request.setLastName("User");
        request.setEmail("user.demo@example.com");

        UserProfile existing = new UserProfile();
        existing.setId(UUID.randomUUID());
        existing.setKeycloakUserId(request.getKeycloakUserId());
        existing.setEmail("user.demo@example.com");

        ProfileResponse response = ProfileResponse.builder()
                .email("user.demo@example.com")
                .build();

        when(repository.findByKeycloakUserId(request.getKeycloakUserId())).thenReturn(Optional.of(existing));
        when(mapper.toResponse(existing)).thenReturn(response);

        ProfileResponse result = service.create(request);

        assertEquals("user.demo@example.com", result.getEmail());
        verify(repository, never()).save(any());
    }

    @Test
    void givenBootstrapPlaceholderProfileWithSameEmail_whenCreateProfile_thenClaimAndUpdateProfile() {

        UUID id = UUID.randomUUID();

        CreateProfileRequest request = new CreateProfileRequest();
        String authenticatedSubject = UUID.randomUUID().toString();
        request.setKeycloakUserId(authenticatedSubject);
        request.setFirstName("Updated");
        request.setLastName("User");
        request.setEmail("user.demo@example.com");
        request.setGender(com.itclinkedin.userprofile.entity.Gender.PREFER_NOT_TO_SAY);
        request.setHeadline("Updated headline");

        UserProfile existing = new UserProfile();
        existing.setId(id);
        existing.setKeycloakUserId(id.toString());
        existing.setEmail("user.demo@example.com");

        ProfileResponse response = ProfileResponse.builder()
                .id(id)
                .keycloakUserId(authenticatedSubject)
                .firstName("Updated")
                .email("user.demo@example.com")
                .build();

        when(repository.findByKeycloakUserId(authenticatedSubject)).thenReturn(Optional.empty());
        when(repository.findByEmailIgnoreCase("user.demo@example.com")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toResponse(existing)).thenReturn(response);

        ProfileResponse result = service.create(request);

        assertEquals(authenticatedSubject, existing.getKeycloakUserId());
        assertEquals("Updated", existing.getFirstName());
        assertEquals("Updated headline", existing.getHeadline());
        assertEquals(authenticatedSubject, result.getKeycloakUserId());
        verify(repository).save(existing);
    }

    @Test
    void givenLegacyUsernameOwnedProfileWithSameEmail_whenCreateProfile_thenClaimWithKeycloakSubject() {

        String authenticatedSubject = UUID.randomUUID().toString();

        CreateProfileRequest request = new CreateProfileRequest();
        request.setKeycloakUserId(authenticatedSubject);
        request.setFirstName("John");
        request.setLastName("Smith");
        request.setEmail("john@test.com");
        request.setGender(com.itclinkedin.userprofile.entity.Gender.MALE);
        request.setHeadline("DevOps Engineer");

        UserProfile existing = new UserProfile();
        existing.setId(UUID.randomUUID());
        existing.setKeycloakUserId("john");
        existing.setEmail("john@test.com");

        ProfileResponse response = ProfileResponse.builder()
                .id(existing.getId())
                .keycloakUserId(authenticatedSubject)
                .firstName("John")
                .email("john@test.com")
                .build();

        when(repository.findByKeycloakUserId(authenticatedSubject)).thenReturn(Optional.empty());
        when(repository.findByEmailIgnoreCase("john@test.com")).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toResponse(existing)).thenReturn(response);

        ProfileResponse result = service.create(request);

        assertEquals(authenticatedSubject, existing.getKeycloakUserId());
        assertEquals("John", existing.getFirstName());
        assertEquals("DevOps Engineer", existing.getHeadline());
        assertEquals(authenticatedSubject, result.getKeycloakUserId());
        verify(repository).save(existing);
    }

    @Test
    void givenMissingKeycloakUserId_whenCreateProfile_thenThrowException() {

        CreateProfileRequest request = new CreateProfileRequest();
        request.setFirstName("Hasnain");
        request.setEmail("hasnain@test.com");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.create(request));

        assertEquals("Authenticated Keycloak user id is required.", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void givenValidId_whenGetProfile_thenReturnProfile() {

        UUID id = UUID.randomUUID();

        UserProfile entity = new UserProfile();
        entity.setId(id);

        ProfileResponse response = ProfileResponse.builder()
                .id(id)
                .email("test@test.com")
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        ProfileResponse result = service.getById(id);

        assertEquals(id, result.getId());
        assertEquals("test@test.com", result.getEmail());

        verify(repository).findById(id);
    }

    @Test
    void givenInvalidId_whenGetProfile_thenThrowException() {

        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getById(id));
    }

    @Test
    void givenValidRequest_whenUpdateProfile_thenSaveUpdatedProfile() {

        UUID id = UUID.randomUUID();
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Shubhra");
        request.setLastName("Tripathi");
        request.setHeadline("Java Full Stack Developer");

        UserProfile entity = new UserProfile();
        entity.setId(id);
        entity.setFirstName("Old");
        entity.setLastName("Name");
        entity.setKeycloakUserId(UUID.randomUUID().toString());

        ProfileResponse response = ProfileResponse.builder()
                .id(id)
                .firstName("Shubhra")
                .lastName("Tripathi")
                .headline("Java Full Stack Developer")
                .build();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        ProfileResponse result = service.update(id, request);

        assertEquals("Shubhra", result.getFirstName());
        assertEquals("Tripathi", result.getLastName());
        assertEquals("Java Full Stack Developer", result.getHeadline());
        verify(repository).save(entity);
    }

    @Test
    void givenValidId_whenDeleteProfile_thenRepositoryCalled() {

        UUID id = UUID.randomUUID();
        UserProfile entity = new UserProfile();
        entity.setId(id);
        entity.setKeycloakUserId(UUID.randomUUID().toString());

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(repository).delete(entity);

        service.delete(id);

        verify(repository, times(1)).delete(entity);
    }
}
