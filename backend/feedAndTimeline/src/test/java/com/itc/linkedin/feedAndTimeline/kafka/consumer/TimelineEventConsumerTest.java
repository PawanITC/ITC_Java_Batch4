package com.itc.linkedin.feedAndTimeline.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itc.linkedin.feedAndTimeline.kafka.avro.PostCreatedAvroMapper;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostCreatedEvent;
import com.itc.linkedin.feedAndTimeline.service.ProcessedEventService;
import com.itc.linkedin.feedAndTimeline.service.TimelineService;
import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimelineEventConsumerTest {

    @Mock
    private TimelineService timelineService;

    @Mock
    private ProcessedEventService processedEventService;

    private ObjectMapper objectMapper;
    private PostCreatedAvroMapper avroMapper;
    private TimelineEventConsumer timelineEventConsumer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        avroMapper = new PostCreatedAvroMapper();
        timelineEventConsumer = new TimelineEventConsumer(timelineService, processedEventService, avroMapper, objectMapper);
    }

    @Test
    void shouldProcessAndMarkNewPostCreatedEvent() {
        PostCreatedEvent event = new PostCreatedEvent(
                "evt-1",
                "post.created",
                1,
                LocalDateTime.now(),
                42L,
                "user.demo",
                "user.demo",
                "LinkedIn Member",
                "Kafka test",
                LocalDateTime.now()
        );
        GenericRecord record = avroMapper.toGenericRecord(event);

        when(processedEventService.isAlreadyProcessed("evt-1")).thenReturn(false);

        timelineEventConsumer.consumePostCreated(record);

        verify(timelineService).handlePostCreated(event);
        verify(processedEventService).markProcessed("post.created", "evt-1", 1);
    }

    @Test
    void shouldSkipDuplicatePostCreatedEvent() {
        PostCreatedEvent event = new PostCreatedEvent(
                "evt-2",
                "post.created",
                1,
                LocalDateTime.now(),
                43L,
                "user.demo",
                "user.demo",
                "LinkedIn Member",
                "duplicate",
                LocalDateTime.now()
        );
        GenericRecord record = avroMapper.toGenericRecord(event);

        when(processedEventService.isAlreadyProcessed("evt-2")).thenReturn(true);

        timelineEventConsumer.consumePostCreated(record);

        verify(timelineService, never()).handlePostCreated(event);
        verify(processedEventService, never()).markProcessed("post.created", "evt-2", 1);
    }

    @Test
    void shouldRejectUnsupportedPostCreatedVersion() {
        PostCreatedEvent event = new PostCreatedEvent(
                "evt-3",
                "post.created",
                2,
                LocalDateTime.now(),
                44L,
                "user.demo",
                "user.demo",
                "LinkedIn Member",
                "bad version",
                LocalDateTime.now()
        );
        GenericRecord record = avroMapper.toGenericRecord(event);

        assertThatThrownBy(() -> timelineEventConsumer.consumePostCreated(record))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported post.created event version");

        verify(timelineService, never()).handlePostCreated(event);
        verify(processedEventService, never()).markProcessed("post.created", "evt-3", 2);
    }
}
