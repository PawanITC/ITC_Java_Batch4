package com.itclinkedin.notification.listener;

import com.itclinkedin.notification.model.Notification;
import com.itclinkedin.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {

    private final NotificationService notificationService;

    public NotificationListener(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "notifications", groupId = "notification-service")
    public void onEvent(String message) {
        System.out.println("🔔 Received event: " + message);
        Notification saved = notificationService.createFromMessage(message);
        System.out.println("Saved notifications id=" + saved.getId() );

    }
}