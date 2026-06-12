package com.itclinkedin.userprofile.mapper;

import com.itclinkedin.userprofile.dto.request.CreateSkillRequest;
import com.itclinkedin.userprofile.dto.response.SkillResponse;
import com.itclinkedin.userprofile.entity.Skill;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    Skill toEntity(CreateSkillRequest request);

    SkillResponse toResponse(Skill skill);
}