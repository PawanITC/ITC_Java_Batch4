package com.itclinkedin.userprofile.controller;

import com.itclinkedin.userprofile.dto.request.CreateExperienceRequest;
import com.itclinkedin.userprofile.dto.response.ExperienceResponse;
import com.itclinkedin.userprofile.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    @PostMapping
    public ResponseEntity<ExperienceResponse> add(@RequestBody CreateExperienceRequest request) {

        ExperienceResponse response = experienceService.addExperience(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
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
    public ResponseEntity<Void> delete(@PathVariable UUID experienceId) {
        experienceService.deleteExperience(experienceId);
        return ResponseEntity.noContent().build();
    }
}