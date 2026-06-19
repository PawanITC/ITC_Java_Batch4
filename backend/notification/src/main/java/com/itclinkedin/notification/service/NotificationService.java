
package com.itclinkedin.notification.service;

import com.itclinkedin.notification.model.Notification;
import com.itclinkedin.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;

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
}
