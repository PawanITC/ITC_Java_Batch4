package com.itclinkedin.userprofile.service;

import com.itclinkedin.userprofile.dto.request.CreateSkillRequest;
import com.itclinkedin.userprofile.dto.response.SkillResponse;

import java.util.List;
import java.util.UUID;

public interface SkillService {

    SkillResponse addSkill(CreateSkillRequest request);

    List<SkillResponse> getByProfileId(UUID profileId);

    SkillResponse updateSkill(UUID skillId, CreateSkillRequest request);

    void deleteSkill(UUID skillId);


}