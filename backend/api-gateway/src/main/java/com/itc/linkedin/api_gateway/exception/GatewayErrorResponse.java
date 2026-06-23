package com.itc.linkedin.api_gateway.exception;

import java.time.LocalDateTime;

public record GatewayErrorResponse(
        boolean success,
        String message,
        String path,
        LocalDateTime timestamp
) {}