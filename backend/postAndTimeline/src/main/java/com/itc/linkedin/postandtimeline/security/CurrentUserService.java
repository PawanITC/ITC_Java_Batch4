package com.itc.linkedin.postandtimeline.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CurrentUserService {

    public String getUserId(Authentication authentication) {
        Jwt jwt = getJwt(authentication);
        if (jwt == null) {
            return null;
        }

        if (StringUtils.hasText(jwt.getSubject())) {
            return jwt.getSubject();
        }

        String preferredUsername = jwt.getClaimAsString("preferred_username");
        if (StringUtils.hasText(preferredUsername)) {
            return preferredUsername;
        }

        return jwt.getClaimAsString("email");
    }

    public String getUsername(Authentication authentication) {
        Jwt jwt = getJwt(authentication);
        if (jwt == null) {
            return null;
        }

        String preferredUsername = jwt.getClaimAsString("preferred_username");
        if (StringUtils.hasText(preferredUsername)) {
            return preferredUsername;
        }

        String name = jwt.getClaimAsString("name");
        if (StringUtils.hasText(name)) {
            return name;
        }

        return jwt.getSubject();
    }

    public String getName(Authentication authentication) {
        Jwt jwt = getJwt(authentication);
        return jwt != null ? jwt.getClaimAsString("name") : null;
    }

    private Jwt getJwt(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }
        return jwt;
    }
}
