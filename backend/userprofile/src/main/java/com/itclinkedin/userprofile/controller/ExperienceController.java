package com.itclinkedin.userprofile.controller;

import com.itclinkedin.userprofile.dto.request.CreateExperienceRequest;
import com.itclinkedin.userprofile.dto.response.ExperienceResponse;
import com.itclinkedin.userprofile.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    @PostMapping
    public ExperienceResponse add(
            @RequestBody CreateExperienceRequest request
    ) {
        return experienceService.addExperience(request);
    }

    @GetMapping("/profile/{profileId}")
    public List<ExperienceResponse> getByProfile(
            @PathVariable UUID profileId
    ) {
        return experienceService.getByProfileId(profileId);
    }

    @PutMapping("/{experienceId}")
    public ExperienceResponse update(
            @PathVariable UUID experienceId,
            @RequestBody CreateExperienceRequest request
    ) {
        return experienceService.updateExperience(
                experienceId,
                request
        );
    }

    @DeleteMapping("/{experienceId}")
    public void delete(
            @PathVariable UUID experienceId
    ) {
        experienceService.deleteExperience(experienceId);
    }
}