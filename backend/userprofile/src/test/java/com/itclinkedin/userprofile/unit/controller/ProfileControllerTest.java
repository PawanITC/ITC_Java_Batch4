package com.itclinkedin.userprofile.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itclinkedin.userprofile.controller.ProfileController;
import com.itclinkedin.userprofile.dto.request.CreateProfileRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;
import com.itclinkedin.userprofile.service.ProfileService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidRequest_whenCreateProfile_thenReturn200() throws Exception {

        CreateProfileRequest request = new CreateProfileRequest();
        request.setFirstName("Hasnain");
        request.setLastName("Ahmad");
        request.setGender(com.itclinkedin.userprofile.entity.Gender.MALE);
        request.setEmail("hasnain@test.com");

        ProfileResponse response = ProfileResponse.builder()
                .id(UUID.randomUUID())
                .email("hasnain@test.com")
                .build();

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("hasnain@test.com"));

        verify(service, times(1)).create(any());
    }

    @Test
    void givenValidId_whenGetProfile_thenReturnProfile() throws Exception {

        UUID id = UUID.randomUUID();

        ProfileResponse response = ProfileResponse.builder()
                .id(id)
                .email("test@test.com")
                .build();

        when(service.getById(id)).thenReturn(response);

        mockMvc.perform(get("/api/profiles/" + id))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("test@test.com"));

        verify(service).getById(id);
    }

    @Test
    void givenValidId_whenDeleteProfile_thenReturn200() throws Exception {

        UUID id = UUID.randomUUID();

        doNothing().when(service).delete(id);

        mockMvc.perform(delete("/api/profiles/" + id))
                .andExpect(status().isOk());

        verify(service).delete(id);
    }
}