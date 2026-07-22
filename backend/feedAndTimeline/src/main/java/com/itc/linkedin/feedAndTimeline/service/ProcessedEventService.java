package com.itc.linkedin.feedAndTimeline.service;

import com.itc.linkedin.feedAndTimeline.entity.ProcessedEvent;
import com.itc.linkedin.feedAndTimeline.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessedEventService {

    private static final String CONSUMER_NAME = "feed-timeline-consumer";

    private final ProcessedEventRepository processedEventRepository;

    public boolean isAlreadyProcessed(String eventId) {
        return StringUtils.hasText(eventId)
                && processedEventRepository.existsByConsumerNameAndEventId(CONSUMER_NAME, eventId);
    }

    @Transactional
    public void markProcessed(String topic, String eventId, Integer eventVersion) {
        if (!StringUtils.hasText(eventId) || isAlreadyProcessed(eventId)) {
            return;
        }

        try {
            processedEventRepository.save(
                    ProcessedEvent.builder()
                            .consumerName(CONSUMER_NAME)
                            .topic(topic)
                            .eventId(eventId)
                            .eventVersion(eventVersion)
                            .processedAt(LocalDateTime.now())
                            .build()
            );
        } catch (DataIntegrityViolationException ex) {
            log.info("Processed event already recorded topic={} eventId={}", topic, eventId);
        }
    }

    public long countProcessedForTopic(String topic) {
        return processedEventRepository.countByTopic(topic);
    }

    public String consumerName() {
        return CONSUMER_NAME;
    }
}
