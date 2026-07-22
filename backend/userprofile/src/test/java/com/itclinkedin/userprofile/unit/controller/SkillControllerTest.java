package com.itclinkedin.userprofile.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itclinkedin.userprofile.controller.SkillController;
import com.itclinkedin.userprofile.dto.request.CreateSkillRequest;
import com.itclinkedin.userprofile.dto.response.SkillResponse;
import com.itclinkedin.userprofile.service.SkillService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SkillController.class)
@AutoConfigureMockMvc(addFilters = false)
class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SkillService skillService;

    private UUID profileId;
    private UUID skillId;

    private CreateSkillRequest request;
    private SkillResponse response;

    @BeforeEach
    void setUp() {
        profileId = UUID.randomUUID();
        skillId = UUID.randomUUID();

        request = new CreateSkillRequest();
        request.setProfileId(profileId);
        request.setSkillName("Java");
        request.setEndorsementCount(5);

        response = new SkillResponse();
        response.setId(skillId);
        response.setSkillName("Java");
        response.setEndorsementCount(5);
    }

    @Test
    void shouldCreateSkillSuccessfully() throws Exception {
        given(skillService.addSkill(any())).willReturn(response);

        mockMvc.perform(post("/api/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skillName").value("Java"));
    }

    @Test
    void shouldFailWhenProfileNotFound() throws Exception {
        given(skillService.addSkill(any()))
                .willThrow(new RuntimeException("Profile not found"));

        mockMvc.perform(post("/api/skills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Profile not found"));
    }

    @Test
    void shouldGetSkillsByProfile() throws Exception {
        given(skillService.getByProfileId(profileId))
                .willReturn(List.of(response));

        mockMvc.perform(get("/api/skills/{profileId}", profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].skillName").value("Java"));
    }

    @Test
    void shouldUpdateSkillSuccessfully() throws Exception {
        given(skillService.updateSkill(eq(skillId), any()))
                .willReturn(response);

        mockMvc.perform(put("/api/skills/{skillId}", skillId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skillName").value("Java"));
    }

    @Test
    void shouldFailWhenSkillNotFound() throws Exception {
        given(skillService.updateSkill(eq(skillId), any()))
                .willThrow(new RuntimeException("Skill not found"));

        mockMvc.perform(put("/api/skills/{skillId}", skillId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Skill not found"));
    }

    @Test
    void shouldDeleteSkillSuccessfully() throws Exception {
        willDoNothing().given(skillService).deleteSkill(skillId);

        mockMvc.perform(delete("/api/skills/{skillId}", skillId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFailWhenDeleteSkillNotFound() throws Exception {
        willThrow(new RuntimeException("Skill not found"))
                .given(skillService).deleteSkill(skillId);

        mockMvc.perform(delete("/api/skills/{skillId}", skillId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Skill not found"));
    }
}