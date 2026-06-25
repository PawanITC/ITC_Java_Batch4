package com.itc.linkedin.feedAndTimeline.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.itc.linkedin.feedAndTimeline.kafka.avro.PostCreatedAvroMapper;
import com.itc.linkedin.feedAndTimeline.kafka.event.CommentCreatedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostCreatedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostDeletedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostLikedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.UserFollowedEvent;
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

    @Test
    void shouldProcessAndMarkUserFollowedEvent() throws Exception {
        UserFollowedEvent event = new UserFollowedEvent(
                "follow-evt-1",
                "follower-1",
                "author-1",
                "Follower",
                "One",
                "follower@example.com",
                "Author",
                "One",
                "author@example.com",
                "2026-06-25T12:00:00"
        );

        when(processedEventService.isAlreadyProcessed("follow-evt-1")).thenReturn(false);

        timelineEventConsumer.consumeUserFollowed(objectMapper.writeValueAsString(event));

        verify(timelineService).handleUserFollowed(event);
        verify(processedEventService).markProcessed("social-follow-events", "follow-evt-1", null);
    }

    @Test
    void shouldProcessPostInteractionEventsWithWireFormatPrefix() throws Exception {
        PostLikedEvent likedEvent = new PostLikedEvent(101L, 77L, "user.demo", 4, LocalDateTime.now());
        CommentCreatedEvent commentEvent = new CommentCreatedEvent(102L, 88L, 77L, "user.demo", "Demo User", 2, LocalDateTime.now());
        PostDeletedEvent deletedEvent = new PostDeletedEvent(103L, 77L, "user.demo", LocalDateTime.now());

        when(processedEventService.isAlreadyProcessed("101")).thenReturn(false);
        when(processedEventService.isAlreadyProcessed("102")).thenReturn(false);
        when(processedEventService.isAlreadyProcessed("103")).thenReturn(false);

        timelineEventConsumer.consumePostLiked(withWirePrefix(objectMapper.writeValueAsString(likedEvent)));
        timelineEventConsumer.consumeCommentCreated(withWirePrefix(objectMapper.writeValueAsString(commentEvent)));
        timelineEventConsumer.consumePostDeleted(withWirePrefix(objectMapper.writeValueAsString(deletedEvent)));

        verify(timelineService).handlePostLiked(likedEvent);
        verify(timelineService).handleCommentCreated(commentEvent);
        verify(timelineService).handlePostDeleted(deletedEvent);
        verify(processedEventService).markProcessed("post.liked", "101", null);
        verify(processedEventService).markProcessed("comment.created", "102", null);
        verify(processedEventService).markProcessed("post.deleted", "103", null);
    }

    private String withWirePrefix(String json) {
        return "\u0000\u0000\u0000\u0002" + json;
    }
}
