package com.itclinkedin.userprofile.controller;

import com.itclinkedin.userprofile.dto.request.CreateLanguageRequest;
import com.itclinkedin.userprofile.dto.response.LanguageResponse;
import com.itclinkedin.userprofile.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/languages")
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @PostMapping
    public LanguageResponse addLanguage(
            @RequestBody CreateLanguageRequest request) {

        return languageService.addLanguage(request);
    }

    @GetMapping("/{profileId}")
    public List<LanguageResponse> getLanguages(
            @PathVariable UUID profileId) {

        return languageService.getByProfileId(profileId);
    }
}