package com.itclinkedin.userprofile.service.impl;

import com.itclinkedin.userprofile.dto.request.CreateProfileRequest;
import com.itclinkedin.userprofile.dto.request.UpdateProfileRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;
import com.itclinkedin.userprofile.entity.UserProfile;
import com.itclinkedin.userprofile.exception.ResourceNotFoundException;
import com.itclinkedin.userprofile.mapper.ProfileMapper;
import com.itclinkedin.userprofile.repository.UserProfileRepository;
import com.itclinkedin.userprofile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserProfileRepository repository;
    private final ProfileMapper mapper;


    @Override
    public ProfileResponse create(CreateProfileRequest request) {

        if (!StringUtils.hasText(request.getKeycloakUserId())) {
            throw new RuntimeException("Authenticated Keycloak user id is required.");
        }

        repository.findByKeycloakUserId(request.getKeycloakUserId())
                .ifPresent(existing -> {
                    throw new RuntimeException("A profile with this Keycloak user already exists.");
                });

        if (repository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new RuntimeException("A profile with this email already exists.");
        }

        UserProfile profile = mapper.toEntity(request);

        return mapper.toResponse(repository.save(profile));
    }

    @Override
    public ProfileResponse getByKeycloakUserId(String keycloakUserId) {
        UserProfile profile = repository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return mapper.toResponse(profile);
    }

    @Override
    public ProfileResponse getById(UUID id) {

        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return mapper.toResponse(profile);
    }

    @Override
    public ProfileResponse update(UUID id, UpdateProfileRequest request) {

        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return updateProfile(profile, request);
    }

    @Override
    public ProfileResponse updateByKeycloakUserId(String keycloakUserId, UpdateProfileRequest request) {
        UserProfile profile = repository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return updateProfile(profile, request);
    }

    private ProfileResponse updateProfile(UserProfile profile, UpdateProfileRequest request) {
        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            profile.setEmail(request.getEmail());
        }
        if (request.getHeadline() != null) {
            profile.setHeadline(request.getHeadline());
        }
        if (request.getAbout() != null) {
            profile.setAbout(request.getAbout());
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getCountry() != null) {
            profile.setCountry(request.getCountry());
        }
        if (request.getProfilePictureUrl() != null) {
            profile.setProfilePictureUrl(request.getProfilePictureUrl());
        }
        if (request.getCoverPhotoUrl() != null) {
            profile.setCoverPhotoUrl(request.getCoverPhotoUrl());
        }
        if (request.getIndustry() != null) {
            profile.setIndustry(request.getIndustry());
        }
        if (request.getCurrentCompany() != null) {
            profile.setCurrentCompany(request.getCurrentCompany());
        }
        if (request.getCurrentPosition() != null) {
            profile.setCurrentPosition(request.getCurrentPosition());
        }
        if (request.getWebsite() != null) {
            profile.setWebsite(request.getWebsite());
        }
        if (request.getGithubUrl() != null) {
            profile.setGithubUrl(request.getGithubUrl());
        }
        if (request.getLinkedinUrl() != null) {
            profile.setLinkedinUrl(request.getLinkedinUrl());
        }
        if (request.getOpenToWork() != null) {
            profile.setOpenToWork(request.getOpenToWork());
        }
        if (request.getProfilePublic() != null) {
            profile.setProfilePublic(request.getProfilePublic());
        }

        return mapper.toResponse(repository.save(profile));
    }

    @Override
    public List<ProfileResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
