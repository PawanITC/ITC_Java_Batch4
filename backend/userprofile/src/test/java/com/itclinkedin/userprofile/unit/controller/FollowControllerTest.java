package com.itclinkedin.userprofile.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itclinkedin.userprofile.controller.FollowController;
import com.itclinkedin.userprofile.dto.request.FollowRequest;
import com.itclinkedin.userprofile.dto.response.ProfileResponse;
import com.itclinkedin.userprofile.service.FollowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FollowController.class)
@AutoConfigureMockMvc(addFilters = false)
class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FollowService followService;

    private FollowRequest request;

    @BeforeEach
    void setUp() {
        request = new FollowRequest();
        request.setFollowerId(UUID.randomUUID());
        request.setFollowingId(UUID.randomUUID());
    }

    // ==========================================
    // SUCCESS PATHS
    // ==========================================

    @Test
    void followUser_Success_Returns200Ok() throws Exception {
        doNothing().when(followService).followUser(any(FollowRequest.class));

        mockMvc.perform(post("/api/follows/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void unfollowUser_Success_Returns200Ok() throws Exception {
        doNothing().when(followService).unfollowUser(any(FollowRequest.class));

        mockMvc.perform(post("/api/follows/unfollow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getFollowerCount_Success_ReturnsCount() throws Exception {
        UUID profileId = UUID.randomUUID();
        when(followService.getFollowerCount(profileId)).thenReturn(5L);

        mockMvc.perform(get("/api/follows/" + profileId + "/followers/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void getFollowingCount_Success_ReturnsCount() throws Exception {
        UUID profileId = UUID.randomUUID();
        when(followService.getFollowingCount(profileId)).thenReturn(10L);

        mockMvc.perform(get("/api/follows/" + profileId + "/following/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    void getFollowers_Success_ReturnsSlice() throws Exception {
        UUID profileId = UUID.randomUUID();
        Slice<ProfileResponse> emptySlice = new SliceImpl<>(Collections.emptyList());
        when(followService.getFollowers(eq(profileId), any(Pageable.class))).thenReturn(emptySlice);

        mockMvc.perform(get("/api/follows/" + profileId + "/followers?page=0&size=10"))
                .andExpect(status().isOk());
    }

    @Test
    void getFollowing_Success_ReturnsSlice() throws Exception {
        UUID profileId = UUID.randomUUID();
        Slice<ProfileResponse> emptySlice = new SliceImpl<>(Collections.emptyList());
        when(followService.getFollowing(eq(profileId), any(Pageable.class))).thenReturn(emptySlice);

        mockMvc.perform(get("/api/follows/" + profileId + "/following?page=0&size=10"))
                .andExpect(status().isOk());
    }

    // ==========================================
    // FAILURE PATHS (Exception Bubble Up)
    // ==========================================

    @Test
    void followUser_Failure_ThrowsException() throws Exception {
        doThrow(new IllegalArgumentException("Users cannot follow themselves"))
                .when(followService).followUser(any(FollowRequest.class));

        mockMvc.perform(post("/api/follows/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFollowers_Failure_ProfileNotFound() throws Exception {
        UUID profileId = UUID.randomUUID();
        when(followService.getFollowers(eq(profileId), any(Pageable.class)))
                .thenThrow(new RuntimeException("Profile not found"));

        mockMvc.perform(get("/api/follows/" + profileId + "/followers"))
                .andExpect(status().isBadRequest());
    }
}