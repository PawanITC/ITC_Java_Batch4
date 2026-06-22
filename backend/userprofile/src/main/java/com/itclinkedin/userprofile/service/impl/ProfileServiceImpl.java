package com.itclinkedin.userprofile.service.impl;

import com.itclinkedin.userprofile.dto.request.CreateProfileRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;
import com.itclinkedin.userprofile.entity.UserProfile;
import com.itclinkedin.userprofile.exception.ResourceNotFoundException;
import com.itclinkedin.userprofile.mapper.ProfileMapper;
import com.itclinkedin.userprofile.repository.UserProfileRepository;
import com.itclinkedin.userprofile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserProfileRepository repository;
    private final ProfileMapper mapper;


    @Override
    public ProfileResponse create(CreateProfileRequest request) {

        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("A profile with this email already exists.");
        }

        UserProfile profile = mapper.toEntity(request);

        return mapper.toResponse(repository.save(profile));
    }

    @Override
    public ProfileResponse getById(UUID id) {

        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return mapper.toResponse(profile);
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