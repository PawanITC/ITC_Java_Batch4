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
        System.out.println("📥 ATTEMPT to process: " + message);
        NotificationEvent event = objectMapper.readValue(message,NotificationEvent.class);
        try {
            Notification saved = notificationService.createFromEvent(event);
            System.out.println("Saved notifications id=" + saved.getId() );
        } catch (DataIntegrityViolationException e) {
            System.out.println("⏭️  Duplicate event " + event.eventId() + " — skipping (already processed)");
        }
        acknowledgment.acknowledge();
    }
    @KafkaListener(topics = "post.liked", groupId = "notification-service")
    public void onPostLiked(String message, Acknowledgment ack) throws Exception {
        PostLikedEvent event = objectMapper.readValue(message, PostLikedEvent.class);

        if (!event.postAuthorId().equals(event.actorUserId())) {
            notificationService.createFromEvent(new NotificationEvent(
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
        CommentCreatedEvent event = objectMapper.readValue(message, CommentCreatedEvent.class);

        if (!event.postAuthorId().equals(event.actorUserId())) {
            notificationService.createFromEvent(new NotificationEvent(
                    String.valueOf(event.eventId()),
                    event.postAuthorId(),
                    "POST_COMMENTED",
                    event.actorName() + " commented on your post"
            ));
        }

        ack.acknowledge();
    }
}