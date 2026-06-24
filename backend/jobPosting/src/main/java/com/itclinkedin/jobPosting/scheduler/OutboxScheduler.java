package com.itclinkedin.jobPosting.scheduler;

import com.itclinkedin.jobPosting.entity.OutboxEvent;
import com.itclinkedin.jobPosting.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;

    // Changed from <String, String> to <String, Object> to align with spring-boot auto-configuration properties
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "job-published-topic";

    @Scheduled(fixedDelay = 5000) // Polls database every 5 seconds
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findByProcessedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : pendingEvents) {
            try {
                // Route record safely out to message system topology layout
                kafkaTemplate.send(TOPIC, event.getAggregateId(), event.getPayload()).get();

                event.setProcessed(true); // Flag event record as shipped successfully
                outboxRepository.save(event);
                log.info("Successfully pushed outbox event metric ID: {} to cluster", event.getId());
            } catch (Exception e) {
                log.error("Failed to accurately dispatch event across cluster boundary line structures: ", e);
            }
        }
    }
}