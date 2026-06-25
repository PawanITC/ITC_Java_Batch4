package com.itclinkedin.notification.repository;
import com.itclinkedin.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientUserId(String recipientUserId);
}