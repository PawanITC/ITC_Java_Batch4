package com.itclinkedin.userprofile.unit.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itclinkedin.userprofile.controller.EducationController;
import com.itclinkedin.userprofile.dto.request.CreateEducationRequest;
import com.itclinkedin.userprofile.dto.response.EducationResponse;
import com.itclinkedin.userprofile.service.EducationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(EducationController.class)
class EducationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EducationService educationService;

    private UUID profileId;
    private UUID educationId;

    private CreateEducationRequest request;
    private EducationResponse response;

    @BeforeEach
    void setUp() {
        profileId = UUID.randomUUID();
        educationId = UUID.randomUUID();

        request = new CreateEducationRequest();
        request.setProfileId(profileId);
        request.setSchoolName("MIT");
        request.setDegree("Masters");
        request.setFieldOfStudy("Computer Science");
        request.setStartYear(2020);
        request.setEndYear(2022);

        response = new EducationResponse();
        response.setId(educationId);
        response.setSchoolName("MIT");
        response.setDegree("Masters");
        response.setFieldOfStudy("Computer Science");
    }

    @Test
    void shouldCreateEducationSuccessfully() throws Exception {
        given(educationService.addEducation(any()))
                .willReturn(response);

        mockMvc.perform(post("/api/educations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schoolName").value("MIT"));
    }

    @Test
    void shouldReturnServerErrorWhenCreateFails() throws Exception {
        given(educationService.addEducation(any()))
                .willThrow(new RuntimeException("Profile not found"));

        mockMvc.perform(post("/api/educations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                // Assert against the JSON structure message property
                .andExpect(jsonPath("$.message").value("Profile not found"));
    }

    @Test
    void shouldGetEducationsByProfile() throws Exception {
        given(educationService.getByProfileId(profileId))
                .willReturn(List.of(response));

        mockMvc.perform(get("/api/educations/profile/{profileId}", profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].schoolName").value("MIT"));
    }

    @Test
    void shouldUpdateEducationSuccessfully() throws Exception {
        given(educationService.updateEducation(eq(educationId), any(CreateEducationRequest.class)))
                .willReturn(response);

        mockMvc.perform(put("/api/educations/{educationId}", educationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schoolName").value("MIT"));
    }

    @Test
    void shouldReturnServerErrorWhenUpdateFails() throws Exception {
        given(educationService.updateEducation(eq(educationId), any(CreateEducationRequest.class)))
                .willThrow(new RuntimeException("Education not found"));

        mockMvc.perform(put("/api/educations/{educationId}", educationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                // Assert against the JSON structure message property
                .andExpect(jsonPath("$.message").value("Education not found"));
    }

    @Test
    void shouldDeleteEducationSuccessfully() throws Exception {
        willDoNothing()
                .given(educationService)
                .deleteEducation(educationId);

        mockMvc.perform(delete("/api/educations/{educationId}", educationId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnServerErrorWhenDeleteFails() throws Exception {
        willThrow(new RuntimeException("Education not found"))
                .given(educationService)
                .deleteEducation(educationId);

        mockMvc.perform(delete("/api/educations/{educationId}", educationId))
                .andExpect(status().isBadRequest())
                // Assert against the JSON structure message property
                .andExpect(jsonPath("$.message").value("Education not found"));
    }
}