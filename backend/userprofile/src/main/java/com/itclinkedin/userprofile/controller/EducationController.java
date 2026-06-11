package com.itclinkedin.userprofile.controller;
import com.itclinkedin.userprofile.dto.request.CreateEducationRequest;
import com.itclinkedin.userprofile.dto.response.EducationResponse;
import com.itclinkedin.userprofile.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    public EducationResponse add(@RequestBody CreateEducationRequest request) {
        return educationService.addEducation(request);
    }

    @GetMapping("/profile/{profileId}")
    public List<EducationResponse> getByProfile(@PathVariable UUID profileId) {
        return educationService.getByProfileId(profileId);
    }

    @PutMapping("/{educationId}")
    public EducationResponse update(
            @PathVariable UUID educationId,
            @RequestBody CreateEducationRequest request
    ) {
        return educationService.updateEducation(
                educationId,
                request
        );
    }

    @DeleteMapping("/{educationId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID educationId
    ) {
        educationService.deleteEducation(educationId);
        return ResponseEntity.noContent().build();
    }
}