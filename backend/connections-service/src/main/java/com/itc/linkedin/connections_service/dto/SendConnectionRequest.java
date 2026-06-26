package com.itc.linkedin.connections_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SendConnectionRequest(
        @Schema(
                description = "User ID of the person who will receive the connection request",
                example = "8f8c9a0e-77e2-41a1-8a11-6a3a33e2c901"
        )
        @NotNull(message = "Receiver ID is required")
        UUID receiverId
) {
}