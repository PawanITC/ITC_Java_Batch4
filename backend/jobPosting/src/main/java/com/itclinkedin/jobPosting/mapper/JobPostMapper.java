package com.itclinkedin.jobPosting.mapper;

import com.itclinkedin.jobPosting.dto.request.JobPostRequest;
import com.itclinkedin.jobPosting.dto.request.JobUpdateRequest;
import com.itclinkedin.jobPosting.dto.response.JobPostResponse;
import com.itclinkedin.jobPosting.entity.JobPost;
import com.itclinkedin.jobPosting.entity.JobBenefit;
import com.itclinkedin.jobPosting.entity.JobRequirement;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobPostMapper {

    @Mapping(target = "requirements", ignore = true)
    @Mapping(target = "benefits", ignore = true)
    JobPost toEntity(JobPostRequest request);

    // New update mapping definition
    @Mapping(target = "requirements", ignore = true)
    @Mapping(target = "benefits", ignore = true)
    void updateEntityFromDto(JobUpdateRequest dto, @MappingTarget JobPost entity);

    @Mapping(target = "benefits", source = "benefits", qualifiedByName = "mapBenefitsToResponse")
    JobPostResponse toResponse(JobPost entity);

    @AfterMapping
    default void linkSubRelations(@MappingTarget JobPost jobPost, JobPostRequest request) {
        if (request.getRequirements() != null) {
            request.getRequirements().forEach(req -> jobPost.getRequirements().add(
                    JobRequirement.builder()
                            .jobPost(jobPost)
                            .requirement(req.getRequirement())
                            .isMandatory(req.getIsMandatory())
                            .build()
            ));
        }
        if (request.getBenefits() != null) {
            request.getBenefits().forEach(ben -> jobPost.getBenefits().add(
                    JobBenefit.builder()
                            .jobPost(jobPost)
                            .benefit(ben)
                            .build()
            ));
        }
    }

    // New AfterMapping to correctly clear and re-link sub-relations during updates
    @AfterMapping
    default void linkSubRelationsForUpdate(@MappingTarget JobPost jobPost, JobUpdateRequest request) {
        if (request.getRequirements() != null) {
            jobPost.getRequirements().clear(); // Clear existing child elements if orphaned-removal is managed
            request.getRequirements().forEach(req -> jobPost.getRequirements().add(
                    JobRequirement.builder()
                            .jobPost(jobPost)
                            .requirement(req.getRequirement())
                            .isMandatory(req.getIsMandatory())
                            .build()
            ));
        }
        if (request.getBenefits() != null) {
            jobPost.getBenefits().clear(); // Clear existing child elements
            request.getBenefits().forEach(ben -> jobPost.getBenefits().add(
                    JobBenefit.builder()
                            .jobPost(jobPost)
                            .benefit(ben)
                            .build()
            ));
        }
    }

    @Named("mapBenefitsToResponse")
    default List<String> mapBenefitsToResponse(List<JobBenefit> benefits) {
        if (benefits == null) return null;
        return benefits.stream().map(JobBenefit::getBenefit).collect(Collectors.toList());
    }
}