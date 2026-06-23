package com.itclinkedin.userprofile.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowedEvent {
    private String eventId;
    private String followerId;
    private String followingId;
    private String followerFirstName;
    private String followerLastName;
    private String followerEmail;
    private String followingFirstName;
    private String followingLastName;
    private String followingEmail;
    private String timestamp;
}