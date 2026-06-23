package com.itclinkedin.userprofile.mapper;

import com.itclinkedin.userprofile.dto.response.FollowerSummaryResponse;
import com.itclinkedin.userprofile.entity.Follow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FollowMapper {

    // Maps the follower profile out of the Follow relationship (Who is following me)
    @Mapping(target = "userId", source = "follower.id")
    @Mapping(target = "firstName", source = "follower.firstName")
    @Mapping(target = "lastName", source = "follower.lastName")
    @Mapping(target = "headline", source = "follower.headline")
    @Mapping(target = "profilePictureUrl", source = "follower.profilePictureUrl")
    FollowerSummaryResponse toFollowerSummary(Follow follow);

    // Maps the following target profile out of the Follow relationship (Who am I following)
    @Mapping(target = "userId", source = "following.id")
    @Mapping(target = "firstName", source = "following.firstName")
    @Mapping(target = "lastName", source = "following.lastName")
    @Mapping(target = "headline", source = "following.headline")
    @Mapping(target = "profilePictureUrl", source = "following.profilePictureUrl")
    FollowerSummaryResponse toFollowingSummary(Follow follow);
}