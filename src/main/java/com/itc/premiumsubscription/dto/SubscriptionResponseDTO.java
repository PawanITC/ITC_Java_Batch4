package com.itc.premiumsubscription.dto;

import com.itc.premiumsubscription.model.SubscriptionStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

public record SubscriptionResponseDTO(UUID id,
                                      UUID userId,
                                      UUID planId,
                                      SubscriptionStatus status,
                                      ZonedDateTime startDate,
                                      ZonedDateTime endDate,
                                      ZonedDateTime nextBillingDate) {
}
