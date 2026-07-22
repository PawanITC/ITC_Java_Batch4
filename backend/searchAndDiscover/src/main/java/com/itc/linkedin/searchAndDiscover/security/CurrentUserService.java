package com.itc.linkedin.searchAndDiscover.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CurrentUserService {

    public String getUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        if (StringUtils.hasText(jwt.getSubject())) {
            return jwt.getSubject();
        }

        String username = jwt.getClaimAsString("preferred_username");
        if (StringUtils.hasText(username)) {
            return username;
        }

        return jwt.getClaimAsString("email");
    }
}
