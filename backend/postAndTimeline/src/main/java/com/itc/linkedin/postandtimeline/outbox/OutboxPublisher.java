package com.itc.linkedin.postandtimeline.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itc.linkedin.postandtimeline.kafka.avro.PostCreatedAvroMapper;
import com.itc.linkedin.postandtimeline.kafka.event.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PostCreatedAvroMapper postCreatedAvroMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events =
                outboxEventRepository.findTop20ByPublishedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(
                        event.getTopic(),
                        String.valueOf(event.getAggregateId()),
                        buildKafkaPayload(event)
                ).get();

                event.setPublished(true);
                event.setPublishedAt(LocalDateTime.now());

                outboxEventRepository.save(event);

                log.info("Published outbox event id={} topic={}", event.getId(), event.getTopic());
            } catch (Exception ex) {
                log.error("Failed to publish outbox event id={} topic={}", event.getId(), event.getTopic(), ex);
            }
        }
    }

    private Object buildKafkaPayload(OutboxEvent outboxEvent) throws Exception {
        if ("post.created".equals(outboxEvent.getEventType())) {
            PostCreatedEvent event =
                    objectMapper.readValue(outboxEvent.getPayload(), PostCreatedEvent.class);
            GenericRecord record = postCreatedAvroMapper.toGenericRecord(event);
            return record;
        }
        return outboxEvent.getPayload();
    }
}
