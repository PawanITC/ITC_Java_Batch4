package com.itc.linkedin.searchAndDiscover.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itc.linkedin.searchAndDiscover.kafka.event.ProfileDeletedEvent;
import com.itc.linkedin.searchAndDiscover.kafka.event.ProfileIndexEvent;
import com.itc.linkedin.searchAndDiscover.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchProfileEventConsumer {

    private final SearchService searchService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = {"profile.created", "profile.updated"},
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeProfileIndexed(String message) throws Exception {
        ProfileIndexEvent event = objectMapper.readValue(message, ProfileIndexEvent.class);
        searchService.indexProfile(event);
        log.info("Indexed {} event in people search profileId={}", event.eventType(), event.profileId());
    }

    @KafkaListener(
            topics = "profile.deleted",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeProfileDeleted(String message) throws Exception {
        ProfileDeletedEvent event = objectMapper.readValue(message, ProfileDeletedEvent.class);
        searchService.deleteProfile(event);
        log.info("Deleted profile from people search profileId={}", event.profileId());
    }
}
