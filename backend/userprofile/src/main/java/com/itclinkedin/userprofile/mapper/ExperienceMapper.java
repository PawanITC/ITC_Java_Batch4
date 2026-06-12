package com.itclinkedin.userprofile.mapper;

import com.itclinkedin.userprofile.dto.request.CreateExperienceRequest;
import com.itclinkedin.userprofile.dto.response.ExperienceResponse;
import com.itclinkedin.userprofile.entity.Experience;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ExperienceMapper {

    @Mapping(target = "userProfile", ignore = true)
    Experience toEntity(CreateExperienceRequest request);

    ExperienceResponse toResponse(Experience experience);
}

