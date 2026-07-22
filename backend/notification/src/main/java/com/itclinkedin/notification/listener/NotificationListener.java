package com.itclinkedin.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itclinkedin.notification.event.CommentCreatedEvent;
import com.itclinkedin.notification.event.NotificationEvent;
import com.itclinkedin.notification.event.PostLikedEvent;
import com.itclinkedin.notification.model.Notification;
import com.itclinkedin.notification.service.NotificationService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NotificationListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public NotificationListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "notifications", groupId = "notification-service")
    public void onEvent(String message, Acknowledgment acknowledgment) throws Exception {
        System.out.println("ATTEMPT to process notification event: " + message);
        NotificationEvent event = objectMapper.readValue(extractJsonPayload(message), NotificationEvent.class);
        createNotification(event);
        acknowledgment.acknowledge();
    }

    @KafkaListener(topics = "post.liked", groupId = "notification-service")
    public void onPostLiked(String message, Acknowledgment ack) throws Exception {
        PostLikedEvent event = objectMapper.readValue(extractJsonPayload(message), PostLikedEvent.class);

        if (event.liked() && !event.postAuthorId().equals(event.actorUserId())) {
            createNotification(new NotificationEvent(
                    String.valueOf(event.eventId()),
                    event.postAuthorId(),
                    "POST_LIKED",
                    event.actorName() + " liked your post"
            ));
        }

        ack.acknowledge();
    }

    @KafkaListener(topics = "comment.created", groupId = "notification-service")
    public void onCommentCreated(String message, Acknowledgment ack) throws Exception {
        CommentCreatedEvent event = objectMapper.readValue(extractJsonPayload(message), CommentCreatedEvent.class);

        if (!event.postAuthorId().equals(event.actorUserId())) {
            createNotification(new NotificationEvent(
                    String.valueOf(event.eventId()),
                    event.postAuthorId(),
                    "POST_COMMENTED",
                    event.actorName() + " commented on your post"
            ));
        }

        ack.acknowledge();
    }

    private void createNotification(NotificationEvent event) {
        try {
            Notification saved = notificationService.createFromEvent(event);
            System.out.println("Saved notification id=" + saved.getId());
        } catch (DataIntegrityViolationException e) {
            System.out.println("Duplicate event " + event.eventId() + " - skipping already processed notification");
        }
    }

    private String extractJsonPayload(String message) {
        if (!StringUtils.hasText(message)) {
            throw new IllegalArgumentException("Kafka message payload is empty");
        }

        int jsonStart = message.indexOf('{');
        if (jsonStart < 0) {
            throw new IllegalArgumentException("Kafka message does not contain a JSON object payload");
        }

        return message.substring(jsonStart);
    }
}
