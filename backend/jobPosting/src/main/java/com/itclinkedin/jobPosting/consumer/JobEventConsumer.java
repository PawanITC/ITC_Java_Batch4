package com.itclinkedin.jobPosting.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobEventConsumer {

    @KafkaListener(topics = "job-published-topic", groupId = "job-posting-group")
    public void listen(String rawJsonPayload) {
        log.info("Consumer intercepted data payload message body cleanly over the wire: {}", rawJsonPayload);
    }
}