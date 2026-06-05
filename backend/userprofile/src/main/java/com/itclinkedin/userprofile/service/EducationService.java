package com.itclinkedin.userprofile.service;

import com.itclinkedin.userprofile.dto.request.CreateEducationRequest;
import com.itclinkedin.userprofile.dto.response.EducationResponse;

import java.util.List;
import java.util.UUID;

public interface EducationService {

    EducationResponse addEducation(CreateEducationRequest request);

    List<EducationResponse> getByProfileId(UUID profileId);
}