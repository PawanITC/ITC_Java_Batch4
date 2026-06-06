package com.itclinkedin.userprofile.mapper;

import com.itclinkedin.userprofile.dto.request.CreateProfileRequest;
import com.itclinkedin.userprofile.dto.response.*;
import com.itclinkedin.userprofile.entity.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    UserProfile toEntity(CreateProfileRequest request);

    ProfileResponse toResponse(UserProfile entity);

    List<EducationResponse> mapEducationList(List<Education> educations);

    List<SkillResponse> mapSkillList(List<Skill> skills);

    List<LanguageResponse> mapLanguageList(List<Language> languages);
    List<ExperienceResponse> mapExperienceList(List<Experience> experiences);

    EducationResponse toEducationResponse(Education education);

    ExperienceResponse toExperienceResponse(Experience experience);
    SkillResponse toSkillResponse(Skill skill);

    LanguageResponse toLanguageResponse(Language language);
}