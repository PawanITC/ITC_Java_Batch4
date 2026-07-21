package com.itclinkedin.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itclinkedin.notification.event.NotificationEvent;
import com.itclinkedin.notification.event.SubscriptionActivatedEvent;
import com.itclinkedin.notification.model.Notification;
import com.itclinkedin.notification.service.NotificationService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionEventListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public SubscriptionEventListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "subscription-events", groupId = "notification-service")
    public void onSubscriptionActivated(String message, Acknowledgment acknowledgment) throws Exception {
        System.out.println("📥 subscription event: " + message);
        SubscriptionActivatedEvent event = objectMapper.readValue(message, SubscriptionActivatedEvent.class);

        // turn the payment event into a notification — REUSE your idempotent create
        NotificationEvent notification = new NotificationEvent(
                "sub-activated-" + event.subscriptionId(),   // unique eventId → dedupes on replay
                event.userId(),                              // who to notify
                "SUBSCRIPTION",                              // type
                "🎉 Welcome to Premium! Your " + event.plan() + " plan is now active."
        );

        try {
            Notification saved = notificationService.createFromEvent(notification);
            System.out.println("✅ welcome notification saved id=" + saved.getId());
        } catch (DataIntegrityViolationException e) {
            System.out.println("⏭️  Duplicate subscription event for sub " + event.subscriptionId() + " — skipping");
        }
        acknowledgment.acknowledge();
    }
}