package com.itc.linkedin.searchAndDiscover.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itc.linkedin.searchAndDiscover.kafka.avro.PostCreatedAvroMapper;
import com.itc.linkedin.searchAndDiscover.kafka.event.CommentCreatedEvent;
import com.itc.linkedin.searchAndDiscover.kafka.event.PostCreatedEvent;
import com.itc.linkedin.searchAndDiscover.kafka.event.PostDeletedEvent;
import com.itc.linkedin.searchAndDiscover.kafka.event.PostLikedEvent;
import com.itc.linkedin.searchAndDiscover.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchPostEventConsumer {

    private final SearchService searchService;
    private final PostCreatedAvroMapper postCreatedAvroMapper;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "post.created",
            containerFactory = "postCreatedKafkaListenerContainerFactory"
    )
    public void consumePostCreated(GenericRecord record) {
        PostCreatedEvent event = postCreatedAvroMapper.fromGenericRecord(record);
        searchService.indexPost(event);
        log.info("Indexed post.created event in search postId={}", event.postId());
    }

    @KafkaListener(
            topics = "post.deleted",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePostDeleted(String message) throws Exception {
        PostDeletedEvent event = objectMapper.readValue(extractJsonPayload(message), PostDeletedEvent.class);
        searchService.deletePost(event);
        log.info("Deleted post from search postId={}", event.postId());
    }

    @KafkaListener(
            topics = "post.liked",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePostLiked(String message) throws Exception {
        PostLikedEvent event = objectMapper.readValue(extractJsonPayload(message), PostLikedEvent.class);
        searchService.updatePostLikes(event);
        log.info("Updated search post likes postId={} likes={}", event.postId(), event.likesCount());
    }

    @KafkaListener(
            topics = "comment.created",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeCommentCreated(String message) throws Exception {
        CommentCreatedEvent event = objectMapper.readValue(extractJsonPayload(message), CommentCreatedEvent.class);
        searchService.updatePostComments(event);
        log.info("Updated search post comments postId={} comments={}", event.postId(), event.commentsCount());
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
