package com.itclinkedin.userprofile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowerSummaryResponse {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String headline;
    private String profilePictureUrl;
}