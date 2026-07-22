package com.itc.linkedin.connections_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

public record ConnectionResponse(
        @Schema(example = "5b3c6f7a-2222-4444-9999-123456789000")
        UUID id,

        @Schema(example = "1a2b3c4d-1111-2222-3333-444455556666")
        UUID requesterId,

        @Schema(example = "8f8c9a0e-77e2-41a1-8a11-6a3a33e2c901")
        UUID receiverId,

        @Schema(example = "PENDING")
        String status,

        @Schema(example = "2026-06-17T12:00:00Z")
        Instant requestedAt,

        @Schema(example = "2026-06-17T12:05:00Z")
        Instant respondedAt
) {
}