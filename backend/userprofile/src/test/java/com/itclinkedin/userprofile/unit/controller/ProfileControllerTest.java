package com.itclinkedin.userprofile.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itclinkedin.userprofile.controller.ProfileController;
import com.itclinkedin.userprofile.dto.request.CreateProfileRequest;
import com.itclinkedin.userprofile.dto.request.UpdateProfileRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;
import com.itclinkedin.userprofile.service.ProfileService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
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

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void whenCreateCurrentUserProfile_thenUseJwtSubjectAsOwner() {
        ProfileController controller = new ProfileController(service);
        CreateProfileRequest request = new CreateProfileRequest();
        request.setKeycloakUserId("malicious-client-value");
        request.setFirstName("Hasnain");
        request.setLastName("Ahmad");
        request.setGender(com.itclinkedin.userprofile.entity.Gender.MALE);
        request.setEmail("hasnain@test.com");

        ProfileResponse response = ProfileResponse.builder()
                .id(UUID.randomUUID())
                .keycloakUserId("jwt-user-id")
                .email("hasnain@test.com")
                .build();

        when(service.create(any())).thenReturn(response);

        controller.create(request, jwt("jwt-user-id"), null, null, null, null);

        var captor = forClass(CreateProfileRequest.class);
        verify(service).create(captor.capture());
        assertThat(captor.getValue().getKeycloakUserId()).isEqualTo("jwt-user-id");
    }

    @Test
    void whenGetCurrentProfile_thenUseJwtSubject() {
        ProfileController controller = new ProfileController(service);
        ProfileResponse response = ProfileResponse.builder()
                .id(UUID.randomUUID())
                .keycloakUserId("jwt-user-id")
                .email("hasnain@test.com")
                .build();

        when(service.getByKeycloakUserId("jwt-user-id")).thenReturn(response);

        ProfileResponse actual = controller.getCurrentProfile(jwt("jwt-user-id"), null, null, null);

        assertThat(actual).isEqualTo(response);
        verify(service).getByKeycloakUserId("jwt-user-id");
    }

    @Test
    void whenJwtSubjectIsMissing_thenUsePreferredUsername() {
        ProfileController controller = new ProfileController(service);
        ProfileResponse response = ProfileResponse.builder()
                .id(UUID.randomUUID())
                .keycloakUserId("user.demo")
                .email("user.demo@example.com")
                .build();

        when(service.getByKeycloakUserId("user.demo")).thenReturn(response);

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("preferred_username", "user.demo")
                .claim("email", "user.demo@example.com")
                .build();

        ProfileResponse actual = controller.getCurrentProfile(jwt, null, null, null);

        assertThat(actual).isEqualTo(response);
        verify(service).getByKeycloakUserId("user.demo");
    }

    @Test
    void whenJwtPrincipalIsNotInjected_thenUseAuthenticationName() {
        ProfileController controller = new ProfileController(service);
        ProfileResponse response = ProfileResponse.builder()
                .id(UUID.randomUUID())
                .keycloakUserId("authenticated-user-id")
                .email("hasnain@test.com")
                .build();

        when(service.getByKeycloakUserId("authenticated-user-id")).thenReturn(response);

        ProfileResponse actual = controller.getCurrentProfile(
                null,
                null,
                null,
                new TestingAuthenticationToken("authenticated-user-id", "token")
        );

        assertThat(actual).isEqualTo(response);
        verify(service).getByKeycloakUserId("authenticated-user-id");
    }

    @Test
    void whenPrincipalAndGatewayHeaderAreMissing_thenUseAuthenticatedBearerTokenSubject() {
        ProfileController controller = new ProfileController(service);
        ProfileResponse response = ProfileResponse.builder()
                .id(UUID.randomUUID())
                .keycloakUserId("token-user-id")
                .email("hasnain@test.com")
                .build();

        when(service.getByKeycloakUserId("token-user-id")).thenReturn(response);

        ProfileResponse actual = controller.getCurrentProfile(
                null,
                null,
                bearerTokenWithSubject("token-user-id"),
                null
        );

        assertThat(actual).isEqualTo(response);
        verify(service).getByKeycloakUserId("token-user-id");
    }

    @Test
    void whenBearerTokenSubjectIsMissing_thenUsePreferredUsernameClaim() {
        ProfileController controller = new ProfileController(service);
        ProfileResponse response = ProfileResponse.builder()
                .id(UUID.randomUUID())
                .keycloakUserId("user.demo")
                .email("user.demo@example.com")
                .build();

        when(service.getByKeycloakUserId("user.demo")).thenReturn(response);

        ProfileResponse actual = controller.getCurrentProfile(
                null,
                null,
                bearerTokenWithClaims(null, "user.demo", "user.demo@example.com"),
                null
        );

        assertThat(actual).isEqualTo(response);
        verify(service).getByKeycloakUserId("user.demo");
    }

    @Test
    void whenMethodAuthenticationIsMissing_thenUseSecurityContextAuthentication() {
        ProfileController controller = new ProfileController(service);
        ProfileResponse response = ProfileResponse.builder()
                .id(UUID.randomUUID())
                .keycloakUserId("security-context-user-id")
                .email("hasnain@test.com")
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("security-context-user-id", "token"));

        when(service.getByKeycloakUserId("security-context-user-id")).thenReturn(response);

        ProfileResponse actual = controller.getCurrentProfile(null, null, null, null);

        assertThat(actual).isEqualTo(response);
        verify(service).getByKeycloakUserId("security-context-user-id");
    }

    @Test
    void givenNoJwt_whenCreateProfile_thenReturn401() throws Exception {

        CreateProfileRequest request = new CreateProfileRequest();
        request.setFirstName("Hasnain");
        request.setLastName("Ahmad");
        request.setGender(com.itclinkedin.userprofile.entity.Gender.MALE);
        request.setEmail("hasnain@test.com");

        ProfileResponse response = ProfileResponse.builder()
                .id(UUID.randomUUID())
                .email("hasnain@test.com")
                .build();

        mockMvc.perform(post("/api/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isUnauthorized());

        verify(service, never()).create(any());
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
    void givenValidRequest_whenUpdateProfile_thenReturnUpdatedProfile() throws Exception {

        UUID id = UUID.randomUUID();
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Shubhra");
        request.setLastName("Tripathi");
        request.setHeadline("Java Full Stack Developer");

        ProfileResponse response = ProfileResponse.builder()
                .id(id)
                .firstName("Shubhra")
                .lastName("Tripathi")
                .headline("Java Full Stack Developer")
                .email("shubhra@test.com")
                .build();

        when(service.update(eq(id), any(UpdateProfileRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/profiles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Shubhra"))
                .andExpect(jsonPath("$.headline").value("Java Full Stack Developer"));

        verify(service).update(eq(id), any(UpdateProfileRequest.class));
    }

    @Test
    void givenValidId_whenDeleteProfile_thenReturn200() throws Exception {

        UUID id = UUID.randomUUID();

        doNothing().when(service).delete(id);

        mockMvc.perform(delete("/api/profiles/" + id))
                .andExpect(status().isOk());

        verify(service).delete(id);
    }

    private Jwt jwt(String subject) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(subject)
                .build();
    }

    private String bearerTokenWithSubject(String subject) {
        return bearerTokenWithClaims(subject, null, null);
    }

    private String bearerTokenWithClaims(String subject, String preferredUsername, String email) {
        String header = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"none\"}".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder payloadJson = new StringBuilder("{");
        appendJsonField(payloadJson, "sub", subject);
        appendJsonField(payloadJson, "preferred_username", preferredUsername);
        appendJsonField(payloadJson, "email", email);
        payloadJson.append("}");

        String payload = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadJson.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return "Bearer " + header + "." + payload + ".signature";
    }

    private void appendJsonField(StringBuilder json, String name, String value) {
        if (value == null) {
            return;
        }

        if (json.length() > 1) {
            json.append(",");
        }

        json.append("\"").append(name).append("\":\"").append(value).append("\"");
    }
}
