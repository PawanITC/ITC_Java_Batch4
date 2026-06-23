package com.itclinkedin.userprofile.controller;

import com.itclinkedin.userprofile.dto.request.CreateProfileRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;
import com.itclinkedin.userprofile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ProfileResponse create(@Valid @RequestBody CreateProfileRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public ProfileResponse getById(@PathVariable UUID id) {
        return service.getById(id);
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