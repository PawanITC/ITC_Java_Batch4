package com.itc.linkedin.connections_service.event;
import com.itc.linkedin.connections_service.entity.OutboxEvent;
import com.itc.linkedin.connections_service.entity.OutboxEventStatus;
import com.itc.linkedin.connections_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Scheduled(fixedDelayString = "${connection-service.outbox.publisher-delay-ms:5000}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByStatusOrderByCreatedAtAsc(
                OutboxEventStatus.PENDING,
                PageRequest.of(0, 50)
        );

        if (events.isEmpty()) {
            return;
        }

        log.info("outbox_publish_started eventCount={}", events.size());

        for (OutboxEvent event : events) {
            try {
                kafkaEventPublisher.publish(
                        event.getEventType(),
                        event.getAggregateId().toString(),
                        event.getPayload()
                );

                event.setStatus(OutboxEventStatus.PUBLISHED);
                event.setPublishedAt(Instant.now());

            } catch (Exception ex) {
                log.error("outbox_publish_failed eventId={} eventType={}",
                        event.getId(), event.getEventType(), ex);

                event.setStatus(OutboxEventStatus.FAILED);
            }
        }

        outboxEventRepository.saveAll(events);

        log.info("outbox_publish_completed eventCount={}", events.size());
    }
}