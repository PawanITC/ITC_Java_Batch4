package com.itclinkedin.userprofile.mapper;

import com.itclinkedin.userprofile.dto.request.CreateProfileRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;
import com.itclinkedin.userprofile.dto.response.EducationResponse;
import com.itclinkedin.userprofile.dto.response.ExperienceResponse;
import com.itclinkedin.userprofile.entity.UserProfile;
import com.itclinkedin.userprofile.entity.Education;
import com.itclinkedin.userprofile.entity.Experience;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    UserProfile toEntity(CreateProfileRequest request);

    ProfileResponse toResponse(UserProfile entity);

    List<EducationResponse> mapEducationList(List<Education> educations);

    List<ExperienceResponse> mapExperienceList(List<Experience> experiences);

    EducationResponse toEducationResponse(Education education);

    ExperienceResponse toExperienceResponse(Experience experience);
}