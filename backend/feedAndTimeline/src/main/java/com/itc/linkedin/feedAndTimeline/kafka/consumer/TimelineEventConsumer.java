package com.itc.linkedin.feedAndTimeline.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itc.linkedin.feedAndTimeline.kafka.avro.PostCreatedAvroMapper;
import com.itc.linkedin.feedAndTimeline.kafka.event.CommentCreatedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostCreatedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostDeletedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.PostLikedEvent;
import com.itc.linkedin.feedAndTimeline.kafka.event.UserFollowedEvent;
import com.itc.linkedin.feedAndTimeline.service.ProcessedEventService;
import com.itc.linkedin.feedAndTimeline.service.TimelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimelineEventConsumer {

    private static final int SUPPORTED_POST_CREATED_VERSION = 1;

    private final TimelineService timelineService;
    private final ProcessedEventService processedEventService;
    private final PostCreatedAvroMapper postCreatedAvroMapper;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "post.created",
            containerFactory = "postCreatedKafkaListenerContainerFactory"
    )
    public void consumePostCreated(GenericRecord record) {
        PostCreatedEvent event = postCreatedAvroMapper.fromGenericRecord(record);

        validatePostCreatedEvent(event);

        if (processedEventService.isAlreadyProcessed(event.eventId())) {
            log.info("Skipping duplicate post.created eventId={}", event.eventId());
            return;
        }

        timelineService.handlePostCreated(event);
        processedEventService.markProcessed("post.created", event.eventId(), event.eventVersion());

        log.info("Successfully processed post.created eventId={} postId={}", event.eventId(), event.postId());
    }

    @KafkaListener(
            topics = "post.deleted",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePostDeleted(String message) throws Exception {
        log.info("Received post.deleted event: {}", message);

        PostDeletedEvent event = objectMapper.readValue(extractJsonPayload(message), PostDeletedEvent.class);
        String eventId = String.valueOf(event.eventId());

        if (processedEventService.isAlreadyProcessed(eventId)) {
            log.info("Skipping duplicate post.deleted eventId={}", event.eventId());
            return;
        }

        timelineService.handlePostDeleted(event);
        processedEventService.markProcessed("post.deleted", eventId, null);

        log.info("Successfully processed post.deleted event for postId={}", event.postId());
    }

    @KafkaListener(
            topics = "post.liked",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePostLiked(String message) throws Exception {
        log.info("Received post.liked event: {}", message);

        PostLikedEvent event = objectMapper.readValue(extractJsonPayload(message), PostLikedEvent.class);
        String eventId = String.valueOf(event.eventId());

        if (processedEventService.isAlreadyProcessed(eventId)) {
            log.info("Skipping duplicate post.liked eventId={}", event.eventId());
            return;
        }

        timelineService.handlePostLiked(event);
        processedEventService.markProcessed("post.liked", eventId, null);

        log.info("Successfully processed post.liked event for postId={}", event.postId());
    }

    @KafkaListener(
            topics = "comment.created",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeCommentCreated(String message) throws Exception {
        log.info("Received comment.created event: {}", message);

        CommentCreatedEvent event = objectMapper.readValue(extractJsonPayload(message), CommentCreatedEvent.class);
        String eventId = String.valueOf(event.eventId());

        if (processedEventService.isAlreadyProcessed(eventId)) {
            log.info("Skipping duplicate comment.created eventId={}", event.eventId());
            return;
        }

        timelineService.handleCommentCreated(event);
        processedEventService.markProcessed("comment.created", eventId, null);

        log.info("Successfully processed comment.created event for postId={}", event.postId());
    }

    @KafkaListener(
            topics = "social-follow-events",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserFollowed(String message) throws Exception {
        log.info("Received social-follow-events event: {}", message);

        UserFollowedEvent event = objectMapper.readValue(message, UserFollowedEvent.class);

        if (!StringUtils.hasText(event.eventId())) {
            throw new IllegalArgumentException("social-follow-events eventId is required");
        }

        if (processedEventService.isAlreadyProcessed(event.eventId())) {
            log.info("Skipping duplicate social-follow-events eventId={}", event.eventId());
            return;
        }

        timelineService.handleUserFollowed(event);
        processedEventService.markProcessed("social-follow-events", event.eventId(), null);

        log.info("Successfully processed social-follow-events eventId={} followerId={} followingId={}",
                event.eventId(), event.followerId(), event.followingId());
    }

    @KafkaListener(
            topics = "social-unfollow-events",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserUnfollowed(String message) throws Exception {
        log.info("Received social-unfollow-events event: {}", message);

        UserFollowedEvent event = objectMapper.readValue(message, UserFollowedEvent.class);

        if (!StringUtils.hasText(event.eventId())) {
            throw new IllegalArgumentException("social-unfollow-events eventId is required");
        }

        if (processedEventService.isAlreadyProcessed(event.eventId())) {
            log.info("Skipping duplicate social-unfollow-events eventId={}", event.eventId());
            return;
        }

        timelineService.handleUserUnfollowed(event);
        processedEventService.markProcessed("social-unfollow-events", event.eventId(), null);

        log.info("Successfully processed social-unfollow-events eventId={} followerId={} followingId={}",
                event.eventId(), event.followerId(), event.followingId());
    }

    private void validatePostCreatedEvent(PostCreatedEvent event) {
        if (!StringUtils.hasText(event.eventId())) {
            throw new IllegalArgumentException("post.created eventId is required");
        }
        if (!"post.created".equals(event.eventType())) {
            throw new IllegalArgumentException("Unexpected event type: " + event.eventType());
        }
        if (event.eventVersion() != SUPPORTED_POST_CREATED_VERSION) {
            throw new IllegalArgumentException("Unsupported post.created event version: " + event.eventVersion());
        }
    }

    private String extractJsonPayload(String message) {
        if (!StringUtils.hasText(message)) {
            throw new IllegalArgumentException("Kafka message payload is empty");
        }

        int jsonStart = message.indexOf('{');
        if (jsonStart < 0) {
            throw new IllegalArgumentException("Kafka message does not contain a JSON object payload");
        }

        return message.substring(jsonStart);
    }
}
