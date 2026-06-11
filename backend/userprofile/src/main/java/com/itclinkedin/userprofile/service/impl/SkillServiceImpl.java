package com.itclinkedin.userprofile.service.impl;
import com.itclinkedin.userprofile.dto.request.CreateSkillRequest;
import com.itclinkedin.userprofile.dto.response.SkillResponse;
import com.itclinkedin.userprofile.entity.Skill;
import com.itclinkedin.userprofile.entity.UserProfile;
import com.itclinkedin.userprofile.mapper.SkillMapper;
import com.itclinkedin.userprofile.repository.SkillRepository;
import com.itclinkedin.userprofile.repository.UserProfileRepository;
import com.itclinkedin.userprofile.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final UserProfileRepository userProfileRepository;
    private final SkillMapper skillMapper;

    @Override
    public SkillResponse addSkill(CreateSkillRequest request) {

        UserProfile profile = userProfileRepository.findById(request.getProfileId())
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        Skill skill = skillMapper.toEntity(request);

        skill.setUserProfile(profile);

        Skill saved = skillRepository.save(skill);

        return skillMapper.toResponse(saved);
    }

    @Override
    public List<SkillResponse> getByProfileId(UUID profileId) {

        return skillRepository.findByUserProfile_Id(profileId)
                .stream()
                .map(skillMapper::toResponse)
                .toList();
    }

    @Override
    public SkillResponse updateSkill(UUID skillId, CreateSkillRequest request) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skill.setSkillName(request.getSkillName());
        skill.setEndorsementCount(request.getEndorsementCount());

        Skill updated = skillRepository.save(skill);

        return skillMapper.toResponse(updated);
    }

    @Override
    public void deleteSkill(UUID skillId) {

        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        skillRepository.delete(skill);
    }
}