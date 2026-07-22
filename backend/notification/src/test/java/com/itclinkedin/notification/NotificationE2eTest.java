package com.itclinkedin.notification;

import com.itclinkedin.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
public class NotificationE2eTest {

    @Container
    @ServiceConnection
    static KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @Autowired KafkaTemplate<Object, Object> kafkaTemplate;
    @Autowired NotificationRepository repository;

    @Test
    void event_publishedToKafka_landsInDatabase() {
        String json = "{\"eventId\":\"e2e-1\",\"recipientUserId\":\"zoe\",\"type\":\"CONNECTION\",\"content\":\"hello e2e\"}";

        kafkaTemplate.send("notifications", json);

        await().atMost(Duration.ofSeconds(20)).untilAsserted(() ->
                assertThat(repository.findByRecipientUserId("zoe")).hasSize(1));
    }
}
