package com.itclinkedin.payment.service;

import com.itclinkedin.payment.model.OutboxEvent;
import com.itclinkedin.payment.repository.OutboxRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxPublisher {

    private static final String TOPIC = "subscription-events";

    private final OutboxRepository outbox;
    private final KafkaTemplate<String, String> kafka;

    public OutboxPublisher(OutboxRepository outbox, KafkaTemplate<String, String> kafka) {
        this.outbox = outbox;
        this.kafka = kafka;
    }

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishPending() {
        List<OutboxEvent> pending = outbox.findByStatus("PENDING");
        for (OutboxEvent event : pending) {
            kafka.send(TOPIC, event.getAggregateId(), event.getPayload());
            event.markSent();
            outbox.save(event);
            System.out.println("📤 published " + event.getEventType() + " (outbox id=" + event.getId() + ") → " + TOPIC);
        }
    }
}