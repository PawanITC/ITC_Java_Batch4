package com.itclinkedin.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itclinkedin.notification.event.NotificationEvent;
import com.itclinkedin.notification.model.Notification;
import com.itclinkedin.notification.service.NotificationService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.Acknowledgment;


@Service
public class NotificationListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public NotificationListener(NotificationService notificationService,ObjectMapper objectMapper){
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "notifications", groupId = "notification-service")
    public void onEvent(String message,Acknowledgment acknowledgment)throws  Exception {
        NotificationEvent event = objectMapper.readValue(message,NotificationEvent.class);
        try {
            Notification saved = notificationService.createFromEvent(event);
            System.out.println("Saved notifications id=" + saved.getId() );
        } catch (DataIntegrityViolationException e) {
            System.out.println("⏭️  Duplicate event " + event.eventId() + " — skipping (already processed)");
        }
        acknowledgment.acknowledge();
    }
}