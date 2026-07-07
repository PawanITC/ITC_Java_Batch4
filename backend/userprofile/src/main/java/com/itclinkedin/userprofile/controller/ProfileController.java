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
            @RequestHeader(value = "Authorization", required = false) String authorization,
            Authentication authentication
    ) {
        request.setKeycloakUserId(requiredSubject(jwt, gatewayUserId, authorization, authentication));
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
        if (jwt != null && jwt.getSubject() != null && !jwt.getSubject().isBlank()) {
            return jwt.getSubject();
        }

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof Jwt principalJwt
                    && principalJwt.getSubject() != null
                    && !principalJwt.getSubject().isBlank()) {
                return principalJwt.getSubject();
            }

            if (authentication.getCredentials() instanceof Jwt credentialsJwt
                    && credentialsJwt.getSubject() != null
                    && !credentialsJwt.getSubject().isBlank()) {
                return credentialsJwt.getSubject();
            }

            String authenticationName = authentication.getName();
            if (authenticationName != null && !authenticationName.isBlank() && !"anonymousUser".equals(authenticationName)) {
                return authenticationName;
            }
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

    private String subjectFromAuthorizationHeader(String authorization) {
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
            String subject = payload.path("sub").asText(null);
            return subject == null || subject.isBlank() ? null : subject;
        } catch (Exception ignored) {
            return null;
        }
    }
}
