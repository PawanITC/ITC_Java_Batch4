package com.itclinkedin.notification.event;

public record NotificationEvent(
        String eventId,
        String recipientUserId,
        String type,
        String content
) {}
