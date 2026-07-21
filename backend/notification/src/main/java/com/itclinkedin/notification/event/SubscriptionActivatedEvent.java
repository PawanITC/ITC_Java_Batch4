package com.itclinkedin.notification.event;

public record SubscriptionActivatedEvent(
        long subscriptionId,
        String userId,
        String plan
) {}
