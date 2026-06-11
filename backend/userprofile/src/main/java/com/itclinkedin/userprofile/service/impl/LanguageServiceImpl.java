package com.itclinkedin.userprofile.service.impl;

import com.itclinkedin.userprofile.dto.request.CreateLanguageRequest;
import com.itclinkedin.userprofile.dto.response.LanguageResponse;
import com.itclinkedin.userprofile.entity.Language;
import com.itclinkedin.userprofile.entity.UserProfile;
import com.itclinkedin.userprofile.mapper.LanguageMapper;
import com.itclinkedin.userprofile.repository.LanguageRepository;
import com.itclinkedin.userprofile.repository.UserProfileRepository;
import com.itclinkedin.userprofile.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final UserProfileRepository userProfileRepository;
    private final LanguageMapper languageMapper;

    @Override
    public LanguageResponse addLanguage(CreateLanguageRequest request) {

        UserProfile profile = userProfileRepository.findById(request.getProfileId())
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        Language language = languageMapper.toEntity(request);

        language.setUserProfile(profile);

        Language saved = languageRepository.save(language);

        return languageMapper.toResponse(saved);
    }

    @Override
    public List<LanguageResponse> getByProfileId(UUID profileId) {

        return languageRepository.findByUserProfile_Id(profileId)
                .stream()
                .map(languageMapper::toResponse)
                .toList();
    }
}