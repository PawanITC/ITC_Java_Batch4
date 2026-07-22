package com.itclinkedin.notification.service;

import com.itclinkedin.notification.event.NotificationEvent;
import com.itclinkedin.notification.model.Notification;
import com.itclinkedin.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)   // turns on Mockito
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;   // a FAKE repository — no real DB

    @InjectMocks
    private NotificationService service;          // the REAL service, with the fake injected in

    @Test
    void savesNotification_forValidEvent() {
        // GIVEN a valid event
        NotificationEvent event = new NotificationEvent("e1", "alice", "CONNECTION", "Bob connected");
        // tell the fake: when save() is called, just return whatever it was handed
        when(repository.save(any(Notification.class)))
                .thenAnswer(call -> call.getArgument(0));

        // WHEN we process it
        Notification result = service.createFromEvent(event);

        // THEN the data is right AND save() was actually called
        assertThat(result.getRecipientUserId()).isEqualTo("alice");
        assertThat(result.getEventId()).isEqualTo("e1");
        verify(repository).save(any(Notification.class));
    }

    @Test
    void throws_andDoesNotSave_whenRecipientMissing() {
        // GIVEN an event missing recipientUserId
        NotificationEvent event = new NotificationEvent("e2", null, "CONNECTION", "x");

        // WHEN/THEN validation rejects it...
        assertThatThrownBy(() -> service.createFromEvent(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("recipientUserId");

        // ...and the bad data is NEVER saved
        verify(repository, never()).save(any());
    }
}
