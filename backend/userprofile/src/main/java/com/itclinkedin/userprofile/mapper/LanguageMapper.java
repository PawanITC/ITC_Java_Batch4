package com.itclinkedin.userprofile.mapper;

import com.itclinkedin.userprofile.dto.request.CreateLanguageRequest;
import com.itclinkedin.userprofile.dto.response.LanguageResponse;
import com.itclinkedin.userprofile.entity.Language;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LanguageMapper {

    Language toEntity(CreateLanguageRequest request);

    LanguageResponse toResponse(Language language);
}