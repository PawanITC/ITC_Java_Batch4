package com.itclinkedin.userprofile.controller;

import com.itclinkedin.userprofile.dto.request.CreateSkillRequest;
import com.itclinkedin.userprofile.dto.response.SkillResponse;
import com.itclinkedin.userprofile.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public SkillResponse addSkill(
            @RequestBody CreateSkillRequest request) {

        return skillService.addSkill(request);
    }

    @GetMapping("/{profileId}")
    public List<SkillResponse> getSkills(
            @PathVariable UUID profileId) {

        return skillService.getByProfileId(profileId);
    }
}