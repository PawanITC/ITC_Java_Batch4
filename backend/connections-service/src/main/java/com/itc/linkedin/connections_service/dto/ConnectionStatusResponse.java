package com.itc.linkedin.connections_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ConnectionStatusResponse(

        @Schema(example = "8f8c9a0e-77e2-41a1-8a11-6a3a33e2c901")
        UUID targetUserId,

        @Schema(example = "CONNECTED")
        RelationshipStatus status,

        @Schema(example = "User is already connected with you")
        String message
) {
}
