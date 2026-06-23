package com.itc.linkedin.postandtimeline.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentUserServiceTest {

    private final CurrentUserService currentUserService = new CurrentUserService();

    @Test
    void shouldUseSubjectAsUserIdWhenPresent() {
        Authentication authentication = authentication(jwtWithSubject("user-1"));

        assertThat(currentUserService.getUserId(authentication)).isEqualTo("user-1");
    }

    @Test
    void shouldFallbackToPreferredUsernameThenEmailForUserId() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("preferred_username", "user.demo")
                .claim("email", "user@example.com")
                .build();

        assertThat(currentUserService.getUserId(authentication(jwt))).isEqualTo("user.demo");

        Jwt emailOnly = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("email", "user@example.com")
                .build();

        assertThat(currentUserService.getUserId(authentication(emailOnly))).isEqualTo("user@example.com");
    }

    @Test
    void shouldResolveUsernameFromPreferredUsernameThenNameThenSubject() {
        Jwt preferred = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("sub-1")
                .claim("preferred_username", "preferred")
                .claim("name", "Full Name")
                .build();
        Jwt nameOnly = Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject("sub-2")
                .claim("name", "Full Name")
                .build();
        Jwt subjectOnly = jwtWithSubject("sub-3");

        assertThat(currentUserService.getUsername(authentication(preferred))).isEqualTo("preferred");
        assertThat(currentUserService.getUsername(authentication(nameOnly))).isEqualTo("Full Name");
        assertThat(currentUserService.getUsername(authentication(subjectOnly))).isEqualTo("sub-3");
    }

    @Test
    void shouldReturnNullWhenAuthenticationDoesNotContainJwt() {
        Authentication authentication = new TestingAuthenticationToken("principal", "cred");

        assertThat(currentUserService.getUserId(authentication)).isNull();
        assertThat(currentUserService.getUsername(authentication)).isNull();
        assertThat(currentUserService.getName(authentication)).isNull();
    }

    private Authentication authentication(Jwt jwt) {
        return new TestingAuthenticationToken(jwt, null);
    }

    private Jwt jwtWithSubject(String subject) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(subject)
                .build();
    }
}
