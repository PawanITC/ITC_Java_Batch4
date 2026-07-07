package com.itclinkedin.userprofile.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itclinkedin.userprofile.dto.request.CreateProfileRequest;
import com.itclinkedin.userprofile.dto.request.UpdateProfileRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;
import com.itclinkedin.userprofile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @GetMapping("/test")
    public String home() {
        return "Hello World";
    }

    @PostMapping
    public ProfileResponse create(
            @Valid @RequestBody CreateProfileRequest request,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = "X-User-Id", required = false) String gatewayUserId,
            @RequestHeader(value = "X-Email", required = false) String gatewayEmail,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            Authentication authentication
    ) {
        request.setKeycloakUserId(requiredSubject(jwt, gatewayUserId, authorization, authentication));
        String authenticatedEmail = emailFromAuthenticatedUser(jwt, gatewayEmail, authorization, authentication);
        if (authenticatedEmail != null) {
            request.setEmail(authenticatedEmail);
        }
        return service.create(request);
    }

    @GetMapping("/me")
    public ProfileResponse getCurrentProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = "X-User-Id", required = false) String gatewayUserId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            Authentication authentication
    ) {
        return service.getByKeycloakUserId(requiredSubject(jwt, gatewayUserId, authorization, authentication));
    }

    @PutMapping("/me")
    public ProfileResponse updateCurrentProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = "X-User-Id", required = false) String gatewayUserId,
            @RequestHeader(value = "Authorization", required = false) String authorization,
            Authentication authentication
    ) {
        return service.updateByKeycloakUserId(requiredSubject(jwt, gatewayUserId, authorization, authentication), request);
    }

    @GetMapping("/{id}")
    public ProfileResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ProfileResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateProfileRequest request) {
        return service.update(id, request);
    }

    @GetMapping
    public List<ProfileResponse> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    private String requiredSubject(Jwt jwt, String gatewayUserId, String authorization, Authentication authentication) {
        String jwtSubject = userIdFromJwt(jwt);
        if (jwtSubject != null) {
            return jwtSubject;
        }

        String authenticationSubject = userIdFromAuthentication(authentication);
        if (authenticationSubject != null) {
            return authenticationSubject;
        }

        String securityContextSubject = userIdFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
        if (securityContextSubject != null) {
            return securityContextSubject;
        }

        if (gatewayUserId != null && !gatewayUserId.isBlank()) {
            return gatewayUserId;
        }

        String tokenSubject = subjectFromAuthorizationHeader(authorization);
        if (tokenSubject != null) {
            return tokenSubject;
        }

        throw new ResponseStatusException(UNAUTHORIZED, "Missing authenticated user id.");
    }

    private String userIdFromAuthentication(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        if (authentication.getPrincipal() instanceof Jwt principalJwt) {
            String principalSubject = userIdFromJwt(principalJwt);
            if (principalSubject != null) {
                return principalSubject;
            }
        }

        if (authentication.getCredentials() instanceof Jwt credentialsJwt) {
            String credentialsSubject = userIdFromJwt(credentialsJwt);
            if (credentialsSubject != null) {
                return credentialsSubject;
            }
        }

        String authenticationName = authentication.getName();
        if (authenticationName != null && !authenticationName.isBlank() && !"anonymousUser".equals(authenticationName)) {
            return authenticationName;
        }

        return null;
    }

    private String userIdFromJwt(Jwt jwt) {
        if (jwt == null) {
            return null;
        }

        return firstPresent(
                jwt.getSubject()
        );
    }

    private String emailFromAuthenticatedUser(
            Jwt jwt,
            String gatewayEmail,
            String authorization,
            Authentication authentication
    ) {
        return firstPresent(
                emailFromJwt(jwt),
                emailFromAuthentication(authentication),
                emailFromAuthentication(SecurityContextHolder.getContext().getAuthentication()),
                gatewayEmail,
                claimFromAuthorizationHeader(authorization, "email")
        );
    }

    private String emailFromAuthentication(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        if (authentication.getPrincipal() instanceof Jwt principalJwt) {
            String email = emailFromJwt(principalJwt);
            if (email != null) {
                return email;
            }
        }

        if (authentication.getCredentials() instanceof Jwt credentialsJwt) {
            return emailFromJwt(credentialsJwt);
        }

        return null;
    }

    private String emailFromJwt(Jwt jwt) {
        return jwt == null ? null : firstPresent(jwt.getClaimAsString("email"));
    }

    private String subjectFromAuthorizationHeader(String authorization) {
        return firstPresent(
                claimFromAuthorizationHeader(authorization, "sub")
        );
    }

    private String claimFromAuthorizationHeader(String authorization, String claimName) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        String token = authorization.substring("Bearer ".length()).trim();
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            return null;
        }

        try {
            byte[] decodedPayload = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode payload = OBJECT_MAPPER.readTree(new String(decodedPayload, StandardCharsets.UTF_8));
            return firstPresent(payload.path(claimName).asText(null));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String firstPresent(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }

        return null;
    }
}
