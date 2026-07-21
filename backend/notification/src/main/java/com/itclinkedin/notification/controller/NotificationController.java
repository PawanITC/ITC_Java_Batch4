package com.itclinkedin.notification.controller;
import com.itclinkedin.notification.model.Notification;
import com.itclinkedin.notification.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import  java.util.List;
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Notification> getForUser(@RequestParam String userId){
        return notificationService.getNotificationsForUser(userId);
    }

    @PutMapping("/{id}/read")
    public Notification markAsRead(@PathVariable Long id){
      return notificationService.markAsRead(id);
    }

}
