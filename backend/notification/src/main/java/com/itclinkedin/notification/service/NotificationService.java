
package com.itclinkedin.notification.service;

import com.itclinkedin.notification.event.NotificationEvent;
import com.itclinkedin.notification.model.Notification;
import com.itclinkedin.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Notification createFromMessage(String message) {
        Notification n = new Notification();
        n.setRecipientUserId("demo-user");   // placeholder for now
        n.setType("MESSAGE");
        n.setContent(message);
        return repository.save(n);           // ← saves to Postgres!
    }

    public Notification createFromEvent(NotificationEvent event){
        if (isBlank(event.eventId()))          throw new IllegalArgumentException("eventId is required");
        if (isBlank(event.recipientUserId()))  throw new IllegalArgumentException("recipientUserId is required");
        if (isBlank(event.type()))             throw new IllegalArgumentException("type is required");
        if (isBlank(event.content()))          throw new IllegalArgumentException("content is required");


        Notification n = new Notification();
        n.setRecipientUserId(event.recipientUserId());
        n.setType(event.type());
        n.setContent(event.content());
        n.setEventId(event.eventId());
        return repository.save(n);
    }

    private Boolean isBlank(String s){
    return s == null || s.isBlank();
    }

    public List<Notification>getNotificationsForUser(String userId){
        return repository.findByRecipientUserId(userId);
    }

    public Notification markAsRead(Long id){
        Notification n = repository.findById(id).orElseThrow();
        n.setRead(true);
        return repository.save(n);
    }

}
