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

    @InjectMocks
    private ProfileServiceImpl service;

    @Test
    void givenValidRequest_whenCreateProfile_thenReturnSavedProfile() {

        CreateProfileRequest request = new CreateProfileRequest();
        request.setKeycloakUserId("keycloak-user-1");
        request.setFirstName("Hasnain");
        request.setEmail("hasnain@test.com");

        UserProfile entity = new UserProfile();

        ProfileResponse response = ProfileResponse.builder()
                .id(UUID.randomUUID())
                .email("hasnain@test.com")
                .build();

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        ProfileResponse result = service.create(request);

        assertNotNull(result);
        assertEquals("hasnain@test.com", result.getEmail());

        verify(repository, times(1)).save(entity);
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

        doNothing().when(repository).deleteById(id);

        service.delete(id);

        verify(repository, times(1)).deleteById(id);
    }
}
