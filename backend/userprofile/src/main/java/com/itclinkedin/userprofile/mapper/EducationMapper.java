package com.itclinkedin.userprofile.mapper;

import com.itclinkedin.userprofile.dto.request.CreateEducationRequest;
import com.itclinkedin.userprofile.dto.response.EducationResponse;
import com.itclinkedin.userprofile.entity.Education;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    Education toEntity(CreateEducationRequest request);

    EducationResponse toResponse(Education entity);
}