package com.itclinkedin.userprofile.controller;

import com.itclinkedin.userprofile.dto.request.CreateProfileRequest;
import com.itclinkedin.userprofile.dto.request.UpdateProfileRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;
import com.itclinkedin.userprofile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;

    @GetMapping("/test")
    public String home() {
        return "Hello World";
    }

    @PostMapping
    public ProfileResponse create(@Valid @RequestBody CreateProfileRequest request, @AuthenticationPrincipal Jwt jwt) {
        if (jwt != null) {
            request.setKeycloakUserId(jwt.getSubject());
        }
        return service.create(request);
    }

    @GetMapping("/me")
    public ProfileResponse getCurrentProfile(@AuthenticationPrincipal Jwt jwt) {
        return service.getByKeycloakUserId(jwt.getSubject());
    }

    @PutMapping("/me")
    public ProfileResponse updateCurrentProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return service.updateByKeycloakUserId(jwt.getSubject(), request);
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
}
