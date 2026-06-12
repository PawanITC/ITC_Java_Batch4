package com.itc.premiumsubscription.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InitiateSubscriptionRequestDTO (
    @NotNull(message = "User ID cannot be null") UUID userId,
    @NotNull(message = "Plan ID cannot be null") UUID planId
){}