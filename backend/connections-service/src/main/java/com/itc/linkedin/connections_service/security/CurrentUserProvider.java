package com.itc.linkedin.connections_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class CurrentUserProvider {

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String userId = jwt.getSubject();

        return UUID.fromString(userId);
       // for testing only need to remove in prod return getRandomLoggedInUserId();
    }
    private static final List<UUID> TEST_USER_IDS = List.of(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            UUID.fromString("22222222-2222-2222-2222-222222222222"),
            UUID.fromString("33333333-3333-3333-3333-333333333333"),
            UUID.fromString("44444444-4444-4444-4444-444444444444"),
            UUID.fromString("55555555-5555-5555-5555-555555555555")
    );

    private static final Random RANDOM = new Random();

    private static UUID getRandomLoggedInUserId() {
        return TEST_USER_IDS.get(RANDOM.nextInt(TEST_USER_IDS.size()));
    }
}
