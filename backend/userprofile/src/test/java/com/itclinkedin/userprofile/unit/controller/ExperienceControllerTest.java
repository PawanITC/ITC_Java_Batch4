package com.itclinkedin.userprofile.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itclinkedin.userprofile.controller.ExperienceController;
import com.itclinkedin.userprofile.dto.request.CreateExperienceRequest;
import com.itclinkedin.userprofile.dto.response.ExperienceResponse;
import com.itclinkedin.userprofile.service.ExperienceService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExperienceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExperienceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExperienceService experienceService;

    @Test
    void createExperience_shouldReturn201() throws Exception {

        CreateExperienceRequest request = new CreateExperienceRequest();
        request.setCompanyName("Google");

        ExperienceResponse response = new ExperienceResponse();
        response.setCompanyName("Google");

        when(experienceService.addExperience(any())).thenReturn(response);

        mockMvc.perform(post("/api/experiences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName").value("Google"));
    }


    @Test
    void getByProfile_shouldReturnList() throws Exception {

        UUID profileId = UUID.randomUUID();

        ExperienceResponse response = new ExperienceResponse();
        response.setCompanyName("Google");

        when(experienceService.getByProfileId(profileId))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/experiences/profile/{profileId}", profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyName").value("Google"));
    }

    @Test
    void updateExperience_shouldReturn200() throws Exception {

        UUID experienceId = UUID.randomUUID();

        CreateExperienceRequest request = new CreateExperienceRequest();
        request.setCompanyName("Amazon");

        ExperienceResponse response = new ExperienceResponse();
        response.setCompanyName("Amazon");

        when(experienceService.updateExperience(eq(experienceId), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/experiences/{experienceId}", experienceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Amazon"));
    }

    @Test
    void deleteExperience_shouldReturn204() throws Exception {

        UUID id = UUID.randomUUID();

        doNothing().when(experienceService).deleteExperience(id);

        mockMvc.perform(delete("/api/experiences/{experienceId}", id))
                .andExpect(status().isNoContent());
    }
}