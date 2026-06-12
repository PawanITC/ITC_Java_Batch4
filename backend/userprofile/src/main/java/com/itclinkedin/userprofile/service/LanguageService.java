package com.itclinkedin.userprofile.service;

import com.itclinkedin.userprofile.dto.request.CreateLanguageRequest;
import com.itclinkedin.userprofile.dto.response.LanguageResponse;

import java.util.List;
import java.util.UUID;

public interface LanguageService {

    LanguageResponse addLanguage(CreateLanguageRequest request);

    List<LanguageResponse> getByProfileId(UUID profileId);
}