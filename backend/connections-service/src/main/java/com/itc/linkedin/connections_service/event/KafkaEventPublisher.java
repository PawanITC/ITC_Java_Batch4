package com.itc.linkedin.connections_service.event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicResolver topicResolver;

    public void publish(String eventType, String key, String payload) {
        String topic = topicResolver.resolveTopic(eventType);

        log.info("kafka_event_publish_started topic={} eventType={} key={}",
                topic, eventType, key);

        kafkaTemplate.send(topic, key, payload);

        log.info("kafka_event_publish_submitted topic={} eventType={} key={}",
                topic, eventType, key);
    }
}
