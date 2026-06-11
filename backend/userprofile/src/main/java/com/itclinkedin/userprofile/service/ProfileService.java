package com.itclinkedin.userprofile.service;

import com.itclinkedin.userprofile.dto.request.CreateProfileRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;

import java.util.List;
import java.util.UUID;

public interface ProfileService {

    ProfileResponse create(CreateProfileRequest request);

    ProfileResponse getById(UUID id);

    List<ProfileResponse> getAll();

    void delete(UUID id);
}