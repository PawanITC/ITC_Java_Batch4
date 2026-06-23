package com.itc.linkedin.postandtimeline.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itc.linkedin.postandtimeline.kafka.avro.PostCreatedAvroMapper;
import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Captor
    private ArgumentCaptor<OutboxEvent> outboxCaptor;

    @Captor
    private ArgumentCaptor<Object> kafkaPayloadCaptor;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final PostCreatedAvroMapper avroMapper = new PostCreatedAvroMapper();

    @Test
    void shouldPublishAndMarkEventAsPublished() {
        OutboxPublisher outboxPublisher =
                new OutboxPublisher(outboxEventRepository, kafkaTemplate, objectMapper, avroMapper);

        OutboxEvent event = OutboxEvent.builder()
                .id(1L)
                .aggregateId(10L)
                .eventType("post.created")
                .topic("post.created")
                .payload("{\"eventId\":\"e1\",\"eventType\":\"post.created\",\"eventVersion\":1,"
                        + "\"occurredAt\":\"2026-06-23T18:00:00\",\"postId\":10,\"authorId\":\"user.demo\","
                        + "\"authorName\":\"user.demo\",\"authorHeadline\":\"LinkedIn Member\","
                        + "\"content\":\"hello\",\"createdAt\":\"2026-06-23T18:00:00\"}")
                .published(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(outboxEventRepository.findTop20ByPublishedFalseOrderByCreatedAtAsc())
                .thenReturn(List.of(event));
        when(kafkaTemplate.send(org.mockito.ArgumentMatchers.eq("post.created"), org.mockito.ArgumentMatchers.eq("10"), org.mockito.ArgumentMatchers.any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        outboxPublisher.publishPendingEvents();

        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq("post.created"), org.mockito.ArgumentMatchers.eq("10"), kafkaPayloadCaptor.capture());
        verify(outboxEventRepository).save(outboxCaptor.capture());

        OutboxEvent savedEvent = outboxCaptor.getValue();
        GenericRecord payload = (GenericRecord) kafkaPayloadCaptor.getValue();
        assertThat(savedEvent.isPublished()).isTrue();
        assertThat(savedEvent.getPublishedAt()).isNotNull();
        assertThat(payload.get("eventId").toString()).isEqualTo("e1");
        assertThat(payload.get("eventVersion")).isEqualTo(1);
    }

    @Test
    void shouldLeaveEventUnpublishedWhenKafkaSendFails() {
        OutboxPublisher outboxPublisher =
                new OutboxPublisher(outboxEventRepository, kafkaTemplate, objectMapper, avroMapper);

        OutboxEvent event = OutboxEvent.builder()
                .id(2L)
                .aggregateId(11L)
                .eventType("post.created")
                .topic("post.created")
                .payload("{\"eventId\":\"e2\",\"eventType\":\"post.created\",\"eventVersion\":1,"
                        + "\"occurredAt\":\"2026-06-23T18:00:00\",\"postId\":11,\"authorId\":\"user.demo\","
                        + "\"authorName\":\"user.demo\",\"authorHeadline\":\"LinkedIn Member\","
                        + "\"content\":\"hello\",\"createdAt\":\"2026-06-23T18:00:00\"}")
                .published(false)
                .createdAt(LocalDateTime.now())
                .build();

        CompletableFuture<Object> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("broker down"));

        when(outboxEventRepository.findTop20ByPublishedFalseOrderByCreatedAtAsc())
                .thenReturn(List.of(event));
        when(kafkaTemplate.send(org.mockito.ArgumentMatchers.eq("post.created"), org.mockito.ArgumentMatchers.eq("11"), org.mockito.ArgumentMatchers.any()))
                .thenReturn((CompletableFuture) failedFuture);

        outboxPublisher.publishPendingEvents();

        verify(outboxEventRepository, never()).save(event);
        assertThat(event.isPublished()).isFalse();
        assertThat(event.getPublishedAt()).isNull();
    }
}
